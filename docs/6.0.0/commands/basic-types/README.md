---
cover: >-
  https://images.unsplash.com/photo-1619363283266-a2380346cbfc?crop=entropy&cs=srgb&fm=jpg&ixid=M3wxOTcwMjR8MHwxfHNlYXJjaHw5fHxzbWFsbCUyMHRyZWV8ZW58MHx8fHwxNzM4MTAxNjE4fDA&ixlib=rb-4.0.3&q=85
coverY: 0
---

# Basic Types

<details>

<summary>Info <mark style="color:yellow;">Sends a message back</mark></summary>

```yaml
command:
  type: "info"
  name: "name"
  message: "Hi there!"
```

```yaml
command:
  type: "info"
  name: "name"
  message:
  - "Line 1"
  - "Line 2"
```

{% code title="Example" %}
```yaml
info:
  type: "info"
  name: "leaf"
  message:
    - "&8&m&l-------&r &a&lLeaf &8&m&l-------"
    - "&7"
    - "&7Velocity Proxy Plugin"
    - "&7Version &f<version>"
    - "&7Author &fSmudge"
    - "&7"
    - "&8&m&l--------------------"
```
{% endcode %}

</details>

