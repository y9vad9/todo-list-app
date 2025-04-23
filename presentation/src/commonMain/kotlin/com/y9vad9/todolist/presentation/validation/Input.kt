package com.y9vad9.todolist.presentation.validation

import com.y9vad9.ktiny.kotlidator.CreationFailure
import com.y9vad9.ktiny.kotlidator.ValidationException
import com.y9vad9.ktiny.kotlidator.ValueFactory
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

sealed interface Input<TRaw> {
    val value: TRaw

    data class Invalid<TRaw>(
        override val value: TRaw,
        val failure: CreationFailure,
    ) : Input<TRaw>

    @JvmInline
    value class Valid<TRaw>(override val value: TRaw) : Input<TRaw>

    @JvmInline
    value class Unknown<TRaw>(override val value: TRaw) : Input<TRaw>

    fun <TBoxed> validated(factory: ValueFactory<TBoxed, TRaw>): Input<TRaw> {
        return factory.create(value).map {
            Valid(value)
        }.getOrElse { Invalid(value, (it as ValidationException).failure) }
    }
}

fun <TRaw> input(value: TRaw): Input<TRaw> = Input.Unknown(value)


@OptIn(ExperimentalContracts::class)
fun <TRaw> Input<TRaw>.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid is Input.Valid<TRaw>)
        returns(false) implies (this@isValid !is Input.Valid<TRaw>)
    }

    return this is Input.Valid<TRaw>
}

@OptIn(ExperimentalContracts::class)
fun <TRaw> Input<TRaw>.isInvalid(): Boolean {
    contract {
        returns(true) implies (this@isInvalid is Input.Invalid<TRaw>)
        returns(false) implies (this@isInvalid !is Input.Invalid<TRaw>)
    }

    return this is Input.Invalid<TRaw>
}