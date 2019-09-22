# VoteBot
[![Build Status](https://travis-ci.org/Votebot/voteBot.svg?branch=master)](https://travis-ci.org/Votebot/voteBot)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a9936806f3cf4f77921e91d9d9eb5fbb)](https://www.codacy.com/app/VoteBot/voteBot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Votebot/voteBot&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/95d417b0cbe6655242c8/maintainability)](https://codeclimate.com/github/Votebot/voteBot/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/95d417b0cbe6655242c8/test_coverage)](https://codeclimate.com/github/Votebot/voteBot/test_coverage)

## Configuration

### Environment Variables
***(R)** = Required*

| Key                       | Description                                                             |
| ---                       | ---                                                                     |
| BOT_ENVIRONMENT           | Bot environment. Can be **development**, **staging** or **production**. |
| BOT_VAULT_ADDRESS         | Address of the [Vault](https://vaultproject.io/) instance.              |
| **(R)** BOT_VAULT_TOKEN   | Vault access token.                                                     |
| BOT_SENTRY_DSN            | Sentry DSN.                                                             |
| **(R)** BOT_DISCORD_TOKEN | Discord bot token.                                                      |
| HTTP_PORT                 | Webserver port.                                                         |

### Vault keys
| Key                   | Description                   |
| ---                   | ---                           |
| sentry_dsn            | Sentry key for error logging. |
| **(R)** discord_token | Discord bot token.            |