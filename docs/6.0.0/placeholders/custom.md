---
cover: >-
  https://images.unsplash.com/photo-1554322662-5b660295377d?crop=entropy&cs=srgb&fm=jpg&ixid=M3wxOTcwMjR8MHwxfHNlYXJjaHw4fHxyb2NrfGVufDB8fHx8MTczODA5ODMyMHww&ixlib=rb-4.0.3&q=85
coverY: 0
---

# Custom

{% hint style="info" %}
Create your own placeholders by adding configuration to the placeholder folder.
{% endhint %}

{% tabs %}
{% tab title="Simple" %}
```yaml
example: "Hi there!"
```

This will create a placeholder with name `{example}` which will be replaced with `Hi there!`
{% endtab %}

{% tab title="Match a String" %}
```yaml
server_formatted:
  condition: "MATCH:<server>"
  options:
    Default: "&f&l<server>"
    "TestingServer": "&e&lTest Server"
```

This will create a placeholder with the name `{server_formatted}`.\
First, it will parse `<server>` which could return for example `TestingServer`.\
The server then checks if it is one of the options, otherwise it will return the default value.\
In this case, it returns `&e&lTest Server`.
{% endtab %}

{% tab title="Check a Players Permission" %}
```yaml
rank:
  condition: "PERMISSION:leaf.rank.?"
  options:
    Default: "&f&lMember"
    "admin": "&c&lAdmin"
```

This will create a placeholder with the name `{rank}`.\
First, it will go through the options and replace the `?` with the option name.\
For example, the first option is `admin`.\
Therefore, if the player has the permission `leaf.rank.admin` it will return `&c&lAdmin`
{% endtab %}
{% endtabs %}

## Copy and Paste Examples

{% code title="Diff color when vanished" %}
```yaml
vanish_colour:
  condition: "MATCH:<vanished>"
  options:
    "true": "&#c0fce6"
    "false": "&#ffffee"
```
{% endcode %}

{% code title="Server name with color" %}
```yaml
server_formatted:
  condition: "MATCH:<server>"
  options:
    Default: "&f&l<server>"
    "TestingServer": "&e&lTest Server"
```
{% endcode %}
