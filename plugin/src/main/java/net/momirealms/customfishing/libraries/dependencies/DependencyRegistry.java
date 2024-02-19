/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customfishing.libraries.dependencies;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import net.momirealms.customfishing.api.data.StorageType;

/**
 * Applies LuckPerms specific behaviour for {@link Dependency}s.
 */
public class DependencyRegistry {

    private static final SetMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableSetMultimap.<StorageType, Dependency>builder()
            .putAll(StorageType.MongoDB,        Dependency.MONGODB_DRIVER_CORE, Dependency.MONGODB_DRIVER_SYNC, Dependency.MONGODB_DRIVER_BSON)
            .putAll(StorageType.MariaDB,        Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE, Dependency.HIKARI, Dependency.MARIADB_DRIVER)
            .putAll(StorageType.MySQL,          Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE, Dependency.HIKARI, Dependency.MYSQL_DRIVER)
            .putAll(StorageType.SQLite,         Dependency.SQLITE_DRIVER)
            .putAll(StorageType.H2,             Dependency.H2_DRIVER)
            .build();

    public DependencyRegistry() {

    }

    public boolean shouldAutoLoad(Dependency dependency) {
        switch (dependency) {
            // all used within 'isolated' classloaders, and are therefore not
            // relocated.
            case ASM:
            case ASM_COMMONS:
            case JAR_RELOCATOR:
            case H2_DRIVER:
            case SQLITE_DRIVER:
                return false;
            default:
                return true;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isGsonRelocated() {
        return JsonElement.class.getName().startsWith("net.momirealms");
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean slf4jPresent() {
        return classExists("org.slf4j.Logger") && classExists("org.slf4j.LoggerFactory");
    }

}