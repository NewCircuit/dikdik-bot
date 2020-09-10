package internal

import (
	"fmt"
	dg "github.com/bwmarrin/discordgo"
	"io/ioutil"
	"log"
	"strings"
	"time"
)

// Variables used for command line parameters
type Bot struct {
	config   DikDikConfig
	client   *dg.Session
	jokes    []string
	facts    []string
	allVars  Variables1
	channels map[string]*ChannelMap
}

// ChannelMap is when two channels are bridged message from a user in the "from" channel get sent
// to the "to" channel.
type ChannelMap struct {
	from     string
	to       string
	user     string
	messages map[string]string
}

type Variables1 struct {
	ourBool bool
	//duration between last message and current message
	between time.Duration
	//time until timeout and deactivate say automatically
	sayoffTime float64
}

func Start(config DikDikConfig) {

	//initialize client
	client, _ := dg.New("Bot " + config.Token)
	//set variables
	varbs := Variables1{
		ourBool:    false,
		sayoffTime: 5,
	}

	client.State.MaxMessageCount = 1000

	//create bot
	bot := Bot{
		config:   config,
		client:   client,
		allVars:  varbs,
		channels: make(map[string]*ChannelMap),
	}

	//loads files
	bot.jokes = readFile(config.JokesPath)
	bot.facts = readFile(config.FactsPath)

	//confirm client opened properly
	if err := client.Open(); err != nil {
		log.Fatalln("Failed to connect to Discord. Is token correct?\n" + err.Error())
	}

	//confirms bot is ready
	fmt.Println("ready your dikdik")

	//initialize handlers
	client.AddHandler(bot.onMessage)
	client.AddHandler(bot.onEdit)
	client.AddHandler(bot.onDelete)
}

//embed for the help menu thing
func (bot Bot) buildEmbed() dg.MessageEmbed {
	embed := dg.MessageEmbed{}
	//check to see if embed is already built
	if bot.allVars.ourBool == false {
		embed.Color = 0x1385ef
		embed.Title = "Commands"
		//only join when first creating
		embed.Description = strings.Replace(
			"`{prefix}talk channelName [message to send to channel]`\n"+
				"Activate message sending to MentionedChannel. All messages you send hereafter will be send to this channel\n"+
				"`{prefix}stop`\n"+
				"Deactivate message sending to MentionChannel\n"+
				"`{prefix}delete`\n"+
				"Delete last sent message while say is active\n"+
				"`{prefix}jokeHere`\n"+
				"Post a joke in current channel\n"+
				"`{prefix}jokeThere MentionChannel`\n"+
				"Send joke to the MentionedChannel\n"+
				"`{prefix}factsHere`\n"+
				"Post facts in current channel\n"+
				"`{prefix}factsThere MentionChannel`\n"+
				"Send facts to the MentionedChannel\n"+
				"`{prefix}status`\n"+
				"Confirm if say is currently active\n"+
				"`{prefix}help`", "{prefix}", bot.config.Prefix, -1)
		bot.allVars.ourBool = true
	}
	return embed
}

func readFile(filename string) []string {
	//read file
	file, err := ioutil.ReadFile(filename)
	//check for errors
	if err != nil {
		log.Fatalf("failed opening file: %s", err)
	}
	//create string to hold file contents
	var str = string(file)
	//split string on comma
	var txtlines = strings.Split(str, "|")
	//return array
	return txtlines[:]
}
