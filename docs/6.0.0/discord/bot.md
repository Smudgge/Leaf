---
cover: >-
  https://images.unsplash.com/photo-1614680376739-414d95ff43df?crop=entropy&cs=srgb&fm=jpg&ixid=M3wxOTcwMjR8MHwxfHNlYXJjaHwxfHxkaXNjb3JkfGVufDB8fHx8MTczODc2OTQ3NXww&ixlib=rb-4.0.3&q=85
coverY: 0
---

# Bot

### Step 1 Create a Bot <a href="#step-1-create-a-bot" id="step-1-create-a-bot"></a>

* Go to the Discord Developer Portal. [https://discord.com/developers/applications](https://discord.com/developers/applications)
* Go to applications.
* Click `new application`.
* Click `Bot.`
* Click `Reset Token`.
* Copy token.
* Enable `Privileged Gateway Intents.` (I recommend enabling them all)

### Step 2 Invite Bot to Server <a href="#step-2-invite-bot-to-server" id="step-2-invite-bot-to-server"></a>

* Open this website [https://discordapi.com/permissions.html](https://discordapi.com/permissions.html)
* Select the permissions the bot should have. (I recommend just giving admin)
* Copy the client ID from the application.
* Click the generated link on the calculator.
* Invite the bot to the server.

### Step 3 Register Bot with Leaf <a href="#step-3-register-bot-with-leaf" id="step-3-register-bot-with-leaf"></a>

* In the `config.yml` paste the bot's token into the `discord_token` field.
* Restart the proxy server.
* You're done!

## Command Defaults

```yaml
command_identnfier:
  ...
  
  # The discord bot section.
  discord_bot:
    # [REQUIRED] → Discord Command Toggle
    enabled: true
    
    # [OPTIONAL] → When a member returns null.
    member_error": "An error occurred while trying to get the member instance."
    
    # [OPTIONAL] → Allowed Channels
    # Specifiy channels that this command
    # is only allowed to be executed in.
    allowed_channels:
      - "1150803388309712947"
    channel_not_allowed: "You cannot run this command in this channel."
    
    # [OPTIONAL] → Discord Permissions.
    # The discord member must have all of
    # these permissions to use this command.
    permissions:
      - ADMINISTRATOR
    no_permission: "You do not have permission to run this command."
    
    # [OPTIONAL] → Discord Roles.
    # The discord member must have at least 1 of 
    # these role names.
    roles:
      - "Owner"
      - "Moderator"
    no_roles": "You do not have the correct roles to run this command."
    
    discord_members:
    - "548674771013861381"
    no_discord_id: "You are not allowed to execute this command"
    
    # [OPTIONAL] → Discord Command Limit
    # Limit the amount of times a discord member can
    # execute this command.
    # No Limit = -1.
    limit: -1
    
    # [OPTIONAL] → Delete after seconds
    # The amount of time the plugin should wait before deleting
    # the discord message that was sent.
    delete_after_seconds: -1
```

## What can i do with my bot?

<table data-view="cards"><thead><tr><th></th><th data-type="content-ref"></th></tr></thead><tbody><tr><td>Create a command that responds with a message.</td><td><a href="../commands/basic-types/info.md#discord-bot">#discord-bot</a></td></tr></tbody></table>





