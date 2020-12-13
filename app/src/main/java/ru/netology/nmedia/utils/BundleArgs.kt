package ru.netology.nmedia.utils

import android.os.Bundle
import ru.netology.nmedia.activity.NewPostFragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


object StringArg: ReadWriteProperty<Bundle, String?> {

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: String?) {
        thisRef.putString(property.name, value)
    }

    override fun getValue(thisRef: Bundle, property: KProperty<*>): String? =
        thisRef.getString(property.name)
}


object BooleanArg: ReadWriteProperty<Bundle, Boolean?> {

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Boolean?) {
        value?.let { thisRef.putBoolean(property.name, it) }
    }

    override fun getValue(thisRef: Bundle, property: KProperty<*>): Boolean? =
        thisRef.getBoolean(property.name)
}


object LongArg: ReadWriteProperty<Bundle, Long?> {

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Long?) {
        value?.let { thisRef.putLong(property.name, it) }
    }

    override fun getValue(thisRef: Bundle, property: KProperty<*>): Long? =
        thisRef.getLong(property.name)
}


object ModeArg: ReadWriteProperty<Bundle, String?> {

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: String?) {
        value?.let { thisRef.putString(property.name, it) }
    }

    override fun getValue(thisRef: Bundle, property: KProperty<*>): String? =
        thisRef.getString(property.name)
}
