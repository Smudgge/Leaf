---
cover: >-
  https://images.unsplash.com/photo-1627347902083-edbcaa5c4286?crop=entropy&cs=srgb&fm=jpg&ixid=M3wxOTcwMjR8MHwxfHNlYXJjaHw1fHxib25zYWl8ZW58MHx8fHwxNzM4MTAwMDI0fDA&ixlib=rb-4.0.3&q=85
coverY: 0
---

# Overview

{% hint style="warning" %}
**Dont want a command?** Delete it from the file and run **/leafreload**. I mean, you can even remove the reload command if you want. :relieved:
{% endhint %}

## Things you can add to any command!

```yaml
# [OPTIONAL] → Command Identifier
# You can remove any command from the configuration to disable it.
# The identifier will not affect the command, it can be named anything! :D
command_identifier:

    # [REQUIRED] → Command Type
    # The command type determines how the command will work.
    type: "info"
    
    # [OPTIONAL] → Command Toggle
    # Disable or enable the command.
    # The value defaults to true.
    enabled: true
    
    # [REQUIRED] → Command Name
    # The name of the command, for example:
    # /[name] [arguments]
    name: "name"
    
    # [OPTIONAL] → Command Aliases
    # Every command can have unlimited aliases.
    # Aliases are alternative command names that will execute the same command.
    aliases: ["name2", "name3"]
    
    # [OPTIONAL] → Command Syntax
    # Lets you override the default syntax.
    # The syntax is shown to the player when they input the
    # incorred arguments.
    syntax: "/[name]"
    
    # [OPTIONAL] → Command Description
    # The commands description. Mainly used
    # when the command is registered with a 
    # discord bot.
    description: "Used to run /[name]"

    # [OPTIONAL] → Command Permission
    # Every command can have permission to execute the command.
    # When this is not set, it will default to everyone.
    permission: "leaf.name"
    
    # [OPTIONAL] → Command Requirements
    # Add a permission requirement for different servers.
    require:
      # The requirements identifier.
      # This can be anything.
      "identifier":
        # The required permission.
        permission: "leaf.bypass"
        # The servers that the permission is required on.
        servers:
        - "ServerName"
    
    # [OPTIONA:] → Player Limit
    # Limit the number of times players can
    # execute the command. This feature requires
    # the database to be enabled.
    # No limit = -1.
    limit: -1
    
    # [OPTIONA:] → Player Cooldown
    # Add a command cooldown, to stop players from spamming
    # commands. This feature requires
    # the database to be enabled.
    # No cooldown = -1.
    # Cooldown is in milliseconds.
    cooldown: -1
```
