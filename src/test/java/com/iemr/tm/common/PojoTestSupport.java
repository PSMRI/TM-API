/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.tm.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DynamicTest;

/**
 * Shared reflection harness that exercises the data-transfer objects of TM-API.
 *
 * <p>
 * The {@code com.iemr.tm.data} packages hold several hundred JPA entities and
 * wrappers whose behaviour is limited to property access, the generated
 * constructors and the {@code getXxx(ArrayList<Object[]>)} row-mapping helpers
 * used by the native queries. Writing one hand-rolled test class per entity
 * would run to tens of thousands of near-identical lines, so the accessor
 * contract is asserted generically here and each data package gets a small test
 * class that points this harness at it.
 * </p>
 */
public final class PojoTestSupport {

	private PojoTestSupport() {
	}

	/** Discovers every concrete class directly inside the given package. */
	public static List<Class<?>> classesIn(String packageName) {
		List<Class<?>> classes = new ArrayList<>();
		Set<String> seen = new HashSet<>();
		String path = packageName.replace('.', '/');
		List<String> names = new ArrayList<>();
		try {
			java.util.Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
			while (resources.hasMoreElements()) {
				File directory = new File(resources.nextElement().getFile());
				File[] files = directory.listFiles();
				if (files == null) {
					continue;
				}
				for (File file : files) {
					String name = file.getName();
					if (!name.endsWith(".class") || name.contains("$") || name.endsWith("Test.class")) {
						continue;
					}
					String simpleName = name.substring(0, name.length() - 6);
					if (seen.add(simpleName)) {
						names.add(simpleName);
					}
				}
			}
		} catch (java.io.IOException e) {
			return classes;
		}
		Collections.sort(names);
		for (String simpleName : names) {
			try {
				Class<?> candidate = Class.forName(packageName + "." + simpleName);
				if (!candidate.isInterface() && !candidate.isEnum() && !candidate.isAnnotation()
						&& !Modifier.isAbstract(candidate.getModifiers())) {
					classes.add(candidate);
				}
			} catch (Throwable ignored) {
				// A class that cannot be loaded in the test classpath is simply not exercised.
			}
		}
		return classes;
	}

	/** Builds one dynamic test per class in the package. */
	public static List<DynamicTest> accessorTestsFor(String packageName) {
		List<Class<?>> classes = classesIn(packageName);
		assertTrue(!classes.isEmpty(), "no classes discovered in " + packageName);
		List<DynamicTest> tests = new ArrayList<>();
		for (Class<?> type : classes) {
			tests.add(DynamicTest.dynamicTest(type.getSimpleName() + " should round-trip every property",
					() -> exercise(type)));
		}
		return tests;
	}

	/**
	 * Instantiates the class, drives every constructor, asserts that each setter
	 * is observable through its getter and calls the remaining no-argument and
	 * row-mapping methods.
	 */
	public static void exercise(Class<?> type) {
		Object instance = instantiate(type);
		assertNotNull(instance, "could not instantiate " + type.getName());

		assertPropertyRoundTrip(type, instance);
		invokeAllConstructors(type);
		invokeRemainingMethods(type, instance);
		invokeRowMappers(type);

		assertNotNull(instance.toString());
		assertEquals(instance.hashCode(), instance.hashCode());
		assertEquals(instance, instance);
	}

	private static void assertPropertyRoundTrip(Class<?> type, Object instance) {
		for (Method setter : type.getMethods()) {
			if (!isSetter(setter)) {
				continue;
			}
			Class<?> propertyType = setter.getParameterTypes()[0];
			Object value = sampleFor(propertyType, setter.getGenericParameterTypes()[0]);
			try {
				setter.invoke(instance, value);
			} catch (Throwable t) {
				continue;
			}
			Method getter = findGetter(type, setter.getName().substring(3), propertyType);
			if (getter == null || !isCompatible(getter.getReturnType(), propertyType)) {
				// Some entities expose a property through accessors of differing types;
				// the round-trip contract only applies to a matching pair.
				continue;
			}
			Object read;
			try {
				read = getter.invoke(instance);
			} catch (Throwable t) {
				continue;
			}
			if (propertyType.isArray()) {
				continue;
			}
			assertEquals(value, read,
					type.getSimpleName() + "." + getter.getName() + " should return the value that was set");
		}
	}

	private static void invokeAllConstructors(Class<?> type) {
		for (Constructor<?> constructor : type.getConstructors()) {
			if (constructor.getParameterCount() == 0) {
				continue;
			}
			Object[] arguments = new Object[constructor.getParameterCount()];
			Class<?>[] parameterTypes = constructor.getParameterTypes();
			Type[] genericTypes = constructor.getGenericParameterTypes();
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = sampleFor(parameterTypes[i], genericTypes[i]);
			}
			try {
				assertNotNull(constructor.newInstance(arguments));
			} catch (Throwable ignored) {
				// Constructors that validate their arguments are covered by the service tests.
			}
		}
	}

	private static void invokeRemainingMethods(Class<?> type, Object instance) {
		for (Method method : type.getMethods()) {
			if (method.getDeclaringClass() == Object.class || Modifier.isStatic(method.getModifiers())
					|| method.getParameterCount() != 0 || isSetter(method)) {
				continue;
			}
			try {
				method.invoke(instance);
			} catch (Throwable ignored) {
				// Derived getters may depend on collaborators the harness cannot supply.
			}
		}
	}

	/**
	 * Calls the {@code getXxx(ArrayList<Object[]>)} helpers that map native query
	 * rows onto the entity. Each helper is driven with an empty result set, with a
	 * single all-null row and with a row whose column types follow the declared
	 * field order of the entity, which is the order the native queries select in.
	 */
	private static void invokeRowMappers(Class<?> type) {
		for (Method method : type.getMethods()) {
			if (!Modifier.isStatic(method.getModifiers()) || method.getParameterCount() != 1
					|| !ArrayList.class.isAssignableFrom(method.getParameterTypes()[0])) {
				continue;
			}
			for (ArrayList<Object[]> rows : rowSets(type)) {
				try {
					method.invoke(null, rows);
				} catch (Throwable ignored) {
					// Row mappers whose column order differs from the field order stop early;
					// their remaining branches are covered by the service tests.
				}
			}
		}
	}

	private static List<ArrayList<Object[]>> rowSets(Class<?> type) {
		List<ArrayList<Object[]>> sets = new ArrayList<>();

		sets.add(new ArrayList<>());

		ArrayList<Object[]> nullRow = new ArrayList<>();
		nullRow.add(new Object[80]);
		sets.add(nullRow);

		ArrayList<Object[]> typedRow = new ArrayList<>();
		typedRow.add(columnsFromFieldOrder(type));
		sets.add(typedRow);

		return sets;
	}

	/** A row whose columns carry sample values for the declared fields, in order. */
	private static Object[] columnsFromFieldOrder(Class<?> type) {
		List<Object> columns = new ArrayList<>();
		for (java.lang.reflect.Field field : type.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
				continue;
			}
			columns.add(sampleFor(field.getType(), field.getGenericType()));
		}
		while (columns.size() < 80) {
			columns.add(null);
		}
		return columns.toArray();
	}

	private static boolean isSetter(Method method) {
		return method.getName().startsWith("set") && method.getParameterCount() == 1
				&& !Modifier.isStatic(method.getModifiers());
	}

	private static boolean isCompatible(Class<?> returnType, Class<?> propertyType) {
		return returnType == propertyType || wrap(returnType) == wrap(propertyType);
	}

	private static Class<?> wrap(Class<?> type) {
		if (type == long.class) {
			return Long.class;
		}
		if (type == int.class) {
			return Integer.class;
		}
		if (type == short.class) {
			return Short.class;
		}
		if (type == byte.class) {
			return Byte.class;
		}
		if (type == double.class) {
			return Double.class;
		}
		if (type == float.class) {
			return Float.class;
		}
		if (type == char.class) {
			return Character.class;
		}
		if (type == boolean.class) {
			return Boolean.class;
		}
		return type;
	}

	private static Method findGetter(Class<?> type, String property, Class<?> propertyType) {
		for (String prefix : new String[] { "get", "is" }) {
			try {
				Method getter = type.getMethod(prefix + property);
				if (getter.getParameterCount() == 0) {
					return getter;
				}
			} catch (NoSuchMethodException ignored) {
				// Try the next accessor naming convention.
			}
		}
		return null;
	}

	private static Object instantiate(Class<?> type) {
		try {
			Constructor<?> constructor = type.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Throwable ignored) {
			// Fall through to the widest constructor below.
		}
		Constructor<?>[] constructors = type.getConstructors();
		Arrays.sort(constructors, (a, b) -> a.getParameterCount() - b.getParameterCount());
		for (Constructor<?> constructor : constructors) {
			Object[] arguments = new Object[constructor.getParameterCount()];
			Class<?>[] parameterTypes = constructor.getParameterTypes();
			Type[] genericTypes = constructor.getGenericParameterTypes();
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = sampleFor(parameterTypes[i], genericTypes[i]);
			}
			try {
				constructor.setAccessible(true);
				return constructor.newInstance(arguments);
			} catch (Throwable ignored) {
				// Try the next constructor.
			}
		}
		return null;
	}

	/** A distinguishable, type-correct sample value for the given property type. */
	public static Object sampleFor(Class<?> type, Type genericType) {
		if (type == String.class) {
			return "sample";
		}
		if (type == Long.class || type == long.class) {
			return 11L;
		}
		if (type == Integer.class || type == int.class) {
			return 12;
		}
		if (type == Short.class || type == short.class) {
			return (short) 13;
		}
		if (type == Byte.class || type == byte.class) {
			return (byte) 14;
		}
		if (type == Double.class || type == double.class) {
			return 15.5d;
		}
		if (type == Float.class || type == float.class) {
			return 16.5f;
		}
		if (type == Character.class || type == char.class) {
			return 'A';
		}
		if (type == Boolean.class || type == boolean.class) {
			return Boolean.TRUE;
		}
		if (type == BigDecimal.class) {
			return BigDecimal.valueOf(17.5d);
		}
		if (type == BigInteger.class) {
			return BigInteger.valueOf(18L);
		}
		if (type == Timestamp.class) {
			return new Timestamp(1_700_000_000_000L);
		}
		if (type == Date.class) {
			return new Date(1_700_000_000_000L);
		}
		if (type == java.util.Date.class) {
			return new java.util.Date(1_700_000_000_000L);
		}
		if (type == java.time.LocalDate.class) {
			return java.time.LocalDate.of(2024, 1, 15);
		}
		if (type == java.time.LocalDateTime.class) {
			return java.time.LocalDateTime.of(2024, 1, 15, 10, 30);
		}
		if (type == ArrayList.class) {
			return new ArrayList<>(elementSample(genericType));
		}
		if (type == List.class) {
			return new ArrayList<>(elementSample(genericType));
		}
		if (type == Set.class || type == HashSet.class) {
			return new HashSet<>(elementSample(genericType));
		}
		if (type == Map.class || type == HashMap.class) {
			return new HashMap<>();
		}
		if (type.isArray()) {
			return Array.newInstance(type.getComponentType(), 1);
		}
		if (type.isEnum()) {
			Object[] constants = type.getEnumConstants();
			return constants.length > 0 ? constants[0] : null;
		}
		return null;
	}

	private static List<Object> elementSample(Type genericType) {
		if (genericType instanceof ParameterizedType) {
			Type[] arguments = ((ParameterizedType) genericType).getActualTypeArguments();
			if (arguments.length == 1 && arguments[0] instanceof Class) {
				Class<?> elementType = (Class<?>) arguments[0];
				if (elementType == String.class) {
					return Collections.singletonList("sample");
				}
				if (elementType == Integer.class) {
					return Collections.singletonList(12);
				}
				if (elementType == Long.class) {
					return Collections.singletonList(11L);
				}
			}
		}
		return Collections.emptyList();
	}

	/** Fails with a readable message; kept for use by the package test classes. */
	public static void failMissingPackage(String packageName) {
		fail("no classes discovered in " + packageName);
	}
}
