# DikDik-Bot
DikDik bot is a Discord bot that says something in another channel. Tell it what to say in channel A and it will send it to channel B. Once say is active all messages will go to channel B until it is deactivated. The bot will automatically deactivate if there is no messages from the sending user for more then 5 minutes.


## [Active Bot](https://discord.com/api/oauth2/authorize?client_id=753666554637451425&permissions=60480&scope=bot)

## Setup
Download [Go](https://go-lang.org/).
```sh
cd ./cmd/DikDik-Bot
go build
./DikDik-Bot
# Setup dikdik-config.yml
```   
        
## Config File
In dikdik-config.yml
```yml
bot_token: <your bot token>
bot_prefix: <prefix>
jokes_path: <path where jokes.txt is located>
facts_path: <path where facts.txt is located>
```
        
## Usage
To display commands type "!help"
