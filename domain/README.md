 Domain Module

The `:domain` module contains the essential business logic and domain model of the application. This module is shared across all platforms (Android, iOS, Desktop, and CLI) and follows Domain-Driven Design (DDD) principles to ensure consistency and maintainability.

## Key Features

- **Domain Models**: The core entities representing business concepts, such as tasks, users, etc.
- **Value Objects**: Immutable objects that are validated during their creation to ensure the integrity of data across the application lifecycle.
- **Validation**: Data validation is handled using custom validation logic, which is implemented in a safe and consistent manner across all platforms. The validation ensures that invalid data does not propagate through the system.

## Example of Domain Model and Value Object

One of the value objects in the `:domain` module is the `TaskName`, which ensures that the name of a task is always within a valid range of characters. Here's an example:

```kotlin
@JvmInline
value class TaskName private constructor(val string: String) {
    companion object {
        val LENGTH_RANGE: IntRange = 1..100

        val factory: ValueFactory<TaskName, String> = factory(
            rules = listOf(StringLengthRangeValidationRule(LENGTH_RANGE)),
            constructor = { TaskName(it) },
        )
    }
}
```

### Value Factory Pattern

The domain model uses a **Value Factory** to create value objects safely. The `ValueFactory` ensures that the data is validated according to specific rules before the object is created.

For example, to create a `TaskName`, you can use the factory's `createOr` method:

```kotlin
val taskName = TaskName.factory.createOr("My Task Name") { failure: CreationFailure ->
    println(it.message)
    return // or default value here
}
```

In this example, if the validation fails, the fallback action will create a default `TaskName`.

### Validation Library

The validation is powered by my custom validation library, [ktiny.kotlidator](https://github.com/y9vad9/ktiny-libs/tree/master/kotlidator), which allows you to define rules for your value objects and validate them safely. It ensures that invalid values are caught early in the creation process, which prevents inconsistent or incorrect data from entering the system.