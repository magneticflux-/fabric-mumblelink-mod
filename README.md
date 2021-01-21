[![](http://cf.way2muchnoise.eu/short_mumble-link-fabric_downloads.svg)](https://minecraft.curseforge.com/projects/mumble-link-fabric)
[![](http://cf.way2muchnoise.eu/versions/mumble-link-fabric_all.svg)](https://minecraft.curseforge.com/projects/mumble-link-fabric)
[![](http://cf.way2muchnoise.eu/packs/short_mumble-link-fabric.svg)](https://minecraft.curseforge.com/projects/mumble-link-fabric)

# fabric-mumblelink-mod
A Fabric mod that connects to the Mumble Link plugin.

Current features:
* Positional audio link to Mumble
* Separation between different dimensions
* Automatic log-in to a server-provided Mumble server
* Separation of team comms / server comms / etc.

Planned features:
* None needed? Please open an issue if you have a use case not covered yet!

---

# For players

## Mumble

1. Install and start Mumble
2. Enable "Link to Game and Transmit Position" in Mumble
3. Join a Mumble server
4. Join a Minecraft world!

### Troubleshooting (IMPORTANT)
If you have issues, try these steps first: https://github.com/magneticflux-/fabric-mumblelink-mod/wiki/Troubleshooting

## TeamSpeak

1. Install and start TeamSpeak
2. Enable the CrossTalk plugin in TeamSpeak
3. Join a Teamspeak server
4. Join a Minecraft world!

# For server operators

The plugin is not required on servers, but having it on the server will allow clients to automatically join a VoIP server of your choosing.

## Configuration

### Trivial
Trivial configuring of the server may be done by specifying the host and port. Clients will simply join the root of the VoIP server. For dimension, team, and server separation, see below.

Dimension separation may also be accomplished by _users_ setting the "Y-Axis multiplier for dimension IDs" to a value above 512. This makes users in the Nether and End appear 512 blocks below and above users in the Overworld, respectively. This approach is not recommended for larger servers, but it may work fine for servers with < 5 people.

### Non-trivial
Most of the non-trivial configuration is done through the use of templated strings. The path and the query string of the URI sent to clients to be opened are template strings, so parts of them may be replaced by user- and server-specific information. Below is a table of available template values:

| Template index | Value source         | Example values |
| -------------- | -------------------- | -------------- |
| `{0}`          | Full dimension ID    | "Minecraft Overworld", "Minecraft The Nether", "Minecraft The End", "Simplevoidworld Void", "Ezwastelands Wastelands" |
| `{1}`          | Dimension namespace  | "Minecraft", "Simplevoidworld", "Ezwastelands" |
| `{2}`          | Dimension path       | "Overworld", "The Nether", "The End" |
| `{3}`          | Team name            | "Red Team", "Team 1", "" |

Some example paths:

- `/My Server/{2}`
  - This routes users to a root channel `/My Server` and a subchannel that matches their dimension. The server should have channels `/My Server/Overworld`, `/My Server/The Nether`, and `/My Server/The End` to support the vanilla Minecraft dimensions.
- `/PvP Teams/{2}/{3}`
  - This routes users to a root channel `/PvP Teams`, a subchannel that matches the dimension, and a sub-subchannel that matches their team name. The server should have channels for all combinations of dimension and team.

If a server does not have a channel for a certain path, the user will remain in their previous channel until their client receives a path that exists.

For more reference on Mumble URIs, see their [wiki page](https://wiki.mumble.info/wiki/Mumble_URL).

For more reference on TeamSpeak URIs, see their [FAQ](https://support.teamspeakusa.com/index.php?/Knowledgebase/Article/View/46/0/how-can-i-link-to-my-teamspeak-3-server-on-my-webpage).

#### More Channel Path Details

In Mumble, the channel path should go in the URI path.

In TeamSpeak, the channel path should go in a query parameter called `channel`.

# Security considerations

Servers with this mod will be able to open Mumble or TeamSpeak URIs through your client. Servers _cannot_ open arbitrary URLs, because only the required information ([host](https://tools.ietf.org/html/rfc3986#section-3.2.2), [port](https://tools.ietf.org/html/rfc3986#section-3.2.3), [path](https://tools.ietf.org/html/rfc3986#section-3.3), and [query](https://tools.ietf.org/html/rfc3986#section-3.4)) is sent to the client. It is important to note that a full URL is _not_ sent to the client; the [scheme](https://tools.ietf.org/html/rfc3986#section-3.1), [user info](https://tools.ietf.org/html/rfc3986#section-3.2.1), and [fragment identifier](https://tools.ietf.org/html/rfc3986#section-3.5) are hard-coded. Only vulnerabilities in Mumble or TeamSpeak may be exploited, and only by servers trusted by the player.

The client-side URI construction is this fragment:
```kotlin
val uri = URI(voipClient.scheme, null, host, port, path, query, null)
```

---

If you feel generous or want to encourage my work, you can throw a few dollars my way here:

[![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/L4L0XZWT)

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=SYSJUAMK9JVWC&currency_code=USD&source=url)
