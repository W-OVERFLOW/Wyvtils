/*
 * Qaltils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Qaltils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.qalcyo.qaltils.config

import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyData
import gg.essential.vigilance.data.PropertyData.Companion.withValue
import gg.essential.vigilance.data.PropertyType
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import java.util.function.Consumer


object ConfigUtils {
    fun createAndRegisterConfig(
        type: PropertyType,
        category: String,
        name: String,
        defaultValue: Any,
        onUpdate: Consumer<Any?>?
    ): PropertyData {
        val config = createConfig(type, category, name, defaultValue, onUpdate)
        register(config)
        return config
    }

    private fun createConfig(
        type: PropertyType,
        category: String,
        name: String,
        defaultValue: Any,
        onUpdate: Consumer<Any?>?
    ): PropertyData {
        val property = createProperty(type, category, name)
        val data = withValue(property, defaultValue, EntityConfig)
        if (onUpdate != null) data.setCallbackConsumer(onUpdate)
        return data
    }

    private fun register(data: PropertyData) {
        EntityConfig.registerProperty(data)
    }

    private fun createProperty(
        type: PropertyType,
        category: String,
        name: String
    ): Property {
        val annotationListener = InvocationHandler { _, method, _ ->
            when (method?.name) {
                "type" -> type
                "category" -> category
                "name" -> name
                else -> method.defaultValue
            }
        }
        return Proxy.newProxyInstance(Property::class.java.classLoader, arrayOf(Property::class.java), annotationListener) as Property
    }

}