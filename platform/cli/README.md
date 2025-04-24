# CLI App

This module provides a native command-line interface for the Todolist Kotlin Multiplatform application. It is designed for users who prefer terminal-based task management or who need lightweight scripting and automation capabilities.

## Architecture

Unlike the other platform targets, the CLI does not depend on any Compose UI or presentation logic. It directly uses the core `:domain` module and its data implementation from `:integration`, making it lightweight and fast.

## Running the App

The CLI app is powered by [Clikt](https://ajalt.github.io/clikt/) and compiled into a native binary using [GraalVM Native Image](https://www.graalvm.org/native-image/). 

To build a native executable:

```bash
./gradlew :platform:cli:nativeCompile
```

To run it without compilation (using the JVM, faster during development):

```bash
./gradlew :platform:cli:run
```

The resulting binary (after `nativeCompile`) can be found in:

```
platform/cli/build/graalvm/native/nativeCompile/
```

## Prebuilt Releases

If you're on **macOS**, you can download the prebuilt CLI binary from the [Releases page](../../releases) and use it directly without compiling from source.

## Usage

Once built or downloaded, the `todolist` command exposes the following interface:

### General

```bash
todolist --help
```

### Commands

| Command         | Description                     |
|-----------------|---------------------------------|
| `list`          | List all tasks                  |
| `view <id>`     | View details of a specific task |
| `create`        | Create a new task               |
| `edit <id>`     | Edit an existing task           |
| `delete <id>`   | Delete a task                   |
| `start <id>`    | Mark a task as in-progress      |
| `complete <id>` | Complete a task                 |

Each command has its own set of options. Run `todolist <command> --help` to view details.

### Example

```bash
todolist create --name="Interview Prep" --description="Finish KMP CLI readme" --due="2025-05-01T15:00"
todolist list --filter=Prep
todolist complete 1
```
You may also reference to the [root README.md](./../../README.md) with the showcase of the CLI app.

## Dependencies

This module uses:
- [Clikt](https://github.com/ajalt/clikt) for CLI parsing
- GraalVM for native binary compilation
- Core logic from `:domain` and `:integration`