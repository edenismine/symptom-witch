# Use just and overmind as the universal task runner

Local development is driven by a root `justfile` and `Procfile` rather than `.vscode/tasks.json`. `just dev` starts all three services (PostgreSQL, Spring Boot API, Vite web) via `overmind`. VS Code debugger configs in `launch.json` are pure attach configs — no `preLaunchTask` — so debugging works independently of the editor.

## Considered Options

- **`.vscode/tasks.json` only** — editor-specific; agents and non-VS Code contributors have no first-class runner
- **`make`** — universally available but designed for artifact-based builds; requires `.PHONY` boilerplate for every task and has surprising tab/whitespace rules
- **`just` + raw `&` shell** — simpler than overmind but Ctrl+C doesn't reliably kill all child processes; mixed stdout is hard to read
- **`just` + `overmind`** — purpose-built task runner + process manager; labeled output per process, clean shutdown, native debugger-attach support via `overmind connect`

## Decision

Use `just` as the task runner (clean syntax, no artifact tracking needed) and `overmind` as the process manager for `just dev` (labeled output, clean Ctrl+C, debugger attach without editor coupling).

The API always starts with JVM debug port 5005 open. There is no meaningful downside for local-only development, and it avoids a separate `dev-debug` vs `dev` distinction.

`.vscode/tasks.json` is deleted. `.vscode/launch.json` is kept as pure attach configurations so VS Code debugging continues to work after `just dev` is running.
