package internal

import (
	dg "github.com/bwmarrin/discordgo"
	"io/ioutil"
	"log"
	"strings"
	"time"
)

// Variables used for command line parameters
type Bot struct {
	config    DikDikConfig
	botPrefix string
	client    *dg.Session
	allVars BingBong
}

type BingBong struct {
	//holds the author name and id of channel they are writing to
	m map[string]string
	//holds the author name and id of last message sent
	dm map[string]string
	//holds the time since last edit
	tm map[string]time.Time
	//holds the author and the id of the channel they are writing in
	cm map[string]string

	ourBool bool

	//duration between last message and current message
	between time.Duration
	//time until timeout and deactivate say automatically
	sayoffTime float64
	//create joke array
	jokelist []string
	//create fact array
	factlist []string
}

func Start(config DikDikConfig) {
	client, _ := dg.New("Bot ", config.Token)

	bingBong := BingBong{
		ourBool: false,
		sayoffTime: 5,
	}

	bot := Bot {
		config: config,
		botPrefix: config.Prefix,
		client: client,
		allVars: bingBong,
	}

	//loads files
	bot.allVars.jokelist = readFile(config.CSVPathJokes)
	bot.allVars.factlist = readFile(config.CSVPathFacts)

	// open websocket connection
	if err := client.Open(); err != nil {
		log.Fatalln("Failed to connect to Discord. Is token correct?\n" + err.Error())
	}

	client.AddHandler(bot.onMessage)
	client.AddHandler(bot.onReady)
	client.AddHandler(bot.onEdit)
}

//embed for the help menu thing
func (bot Bot) buildEmbed(s string, cmd []string) dg.MessageEmbed {
	//check to see if embed is already built
	if bot.allVars.ourBool == false {
		embed := dg.MessageEmbed{}
		embed.Color = 0x1385ef
		embed.Title = s
		//only join when first creating
		cmd[0] = strings.Join(cmd[0:], " \n")
		embed.Description = cmd[0]
		bot.allVars.ourBool = true
		return embed
	} else {
		embed := dg.MessageEmbed{}
		embed.Color = 0x1385ef
		embed.Title = s
		embed.Description = cmd[0]
		return embed

	}
}

func readFile(filename string)[]string{
	//read file
	file, err := ioutil.ReadFile(filename)
	//check for errors
	if err != nil {
		log.Fatalf("failed opening file: %s", err)
	}
	//create string to hold file contents
	var str = string(file)
	//split string on comma
	var txtlines = strings.Split(str,",")
	//return array
	return txtlines[:]
}
