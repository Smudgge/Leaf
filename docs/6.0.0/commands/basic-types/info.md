---
icon: circle-info
cover: >-
  https://images.unsplash.com/photo-1699556587239-9fad6974d3be?crop=entropy&cs=srgb&fm=jpg&ixid=M3wxOTcwMjR8MHwxfHNlYXJjaHw0fHxzaW1wbGUlMjBtb3VudGFpbnxlbnwwfHx8fDE3Mzg3NjgwMTd8MA&ixlib=rb-4.0.3&q=85
coverY: 0
---

# Info

{% hint style="success" %}
Run command, get answer. As simple as it gets.
{% endhint %}

{% tabs %}
{% tab title="Simple" %}
```yaml
example_command:
  type: "info"
  name: "example"
  message: "Hi there!"
```

{% code title="/example" %}
```
Hi there!
```
{% endcode %}

***

```yaml
example_command:
  type: "info"
  name: "example"
  message:
  - "First line"
  - "Second line"
```

{% code title="/example" %}
```yaml
First line
Second line
```
{% endcode %}
{% endtab %}

{% tab title="Pages" %}
```yaml
example_command:
  type: "info"
  name: "example"
  message: "Please specify a page."
  pages:
    "1": "Hi"
    "lines": 
    - "Line 1"
    - "Line 2"
  default: "This page named %page% doesn't exist."
```

{% code title="/example" %}
```
Please specify a page.
```
{% endcode %}

{% code title="/example 1" %}
```
Hi
```
{% endcode %}

{% code title="/example lines" %}
```
Line 1
Line 2
```
{% endcode %}

{% code title="/example hi" %}
```
This page named %page% doesn't exist.
```
{% endcode %}
{% endtab %}

{% tab title="Discord Bot" %}
{% hint style="info" %}
For all embed options click here: &#x20;
{% endhint %}

```yaml
example_command:
  type: "info"
  name: "example"
  message: "Hi there!"
  discord_bot:
    argument_name: "page"
    argument_description: "The page that will be displayed."
    message: "Please specify a page."
    pages:
      "1": "Hi"
      "lines": 
      - "Line 1"
      - "Line 2"
      "embed":
        embeds:
          1:
            title:
              message: "Leaf"
            description:  
              - "**Velocity Proxy Plugin**"
              - "Version `<version>`"
              - "Author `Smudge`"
    default: "This page named %page% doesn't exist."
```

{% code title="/example 1" %}
```
Hi
```
{% endcode %}
{% endtab %}
{% endtabs %}

