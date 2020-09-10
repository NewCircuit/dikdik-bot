package internal

import (
	"fmt"
	util "github.com/Floor-Gang/utilpkg/botutil"
	dg "github.com/bwmarrin/discordgo"
	"log"
	"math/rand"
	"time"
)

//help command
func (bot Bot) onHelp(msg *dg.MessageCreate) {
	//build string to display help/info/commands
	embed := bot.buildEmbed()
	_, err := bot.client.ChannelMessageSendEmbed(msg.ChannelID, &embed)
	if err != nil {
		fmt.Println(err)
	}
}

//jokeHere command
func (bot Bot) onJoke(msg *dg.MessageCreate, args []string) {
	//creates a random seed
	rand.Seed(time.Now().UnixNano())
	randomIndex := rand.Intn(len(bot.jokes))
	channelID := msg.ChannelID

	if len(args) > 0 {
		channelID = util.FilterTag(args[1])
	}

	_, err := bot.client.ChannelMessageSend(channelID, bot.jokes[randomIndex])
	if err != nil {
		fmt.Println(err)
	}
}

//factsHere command
func (bot Bot) onFact(msg *dg.MessageCreate, args []string) {
	//creates a random seed
	rand.Seed(time.Now().UnixNano())
	randomIndex := rand.Intn(len(bot.facts))
	channelID := msg.ChannelID

	if len(args) > 0 {
		channelID = util.FilterTag(args[0])
	}

	_, err := bot.client.ChannelMessageSend(channelID, bot.facts[randomIndex])
	if err != nil {
		log.Println("Failed to send fact", err)
		bot.bad(msg.Message)
	} else {
		bot.good(msg.Message)
	}
}

//+say command
func (bot Bot) onSet(msg *dg.MessageCreate, args []string) {
	if len(args) > 0 {
		channelID := util.FilterTag(args[0])
		_, err := bot.client.Channel(channelID)

		if err != nil {
			log.Println("Couldn't find " + args[0])
			bot.bad(msg.Message)
			return
		}

		channelMap := &ChannelMap{
			from:     msg.ChannelID,
			to:       channelID,
			user:     msg.Author.ID,
			messages: make(map[string]string),
		}

		bot.channels[channelMap.user] = channelMap

		bot.good(msg.Message)

		return
	}
	bot.bad(msg.Message)
}

//text while say command is active
func (bot Bot) onText(msg *dg.MessageCreate, channelMap *ChannelMap) {
	_, err := bot.client.Channel(channelMap.to)

	if err != nil {
		delete(bot.channels, msg.Author.ID)
		return
	}

	msgSent, err := bot.client.ChannelMessageSend(channelMap.to, msg.Content)

	if err == nil {
		channelMap.messages[msg.ID] = msgSent.ID
		bot.sent(msg.Message)
	} else {
		bot.bad(msg.Message)
	}
}

//-say command
func (bot Bot) onUnset(msg *dg.MessageCreate) {
	_, isOK := bot.channels[msg.Author.ID]

	if !isOK {
		return
	}

	delete(bot.channels, msg.Author.ID)
}

//checks if say is active
func (bot Bot) onStatus(msg *dg.MessageCreate) {
	// //if user exists in map say active
	// if err, exists := bot.allVars.m[msg.Author.Username]; exists {
	// 	if err != "" {
	// 		fmt.Println(err)
	// 	}
	// 	_, err := bot.client.ChannelMessageSend(
	// 		msg.ChannelID,
	// 		"Say is currently active for "+msg.Author.Username+" in channel "+
	// 			"<#"+bot.allVars.m[msg.Author.Username]+">",
	// 	)
	// 	if err != nil {
	// 		fmt.Println(err)
	// 	}
	// 	_, errd := bot.client.ChannelMessageSend(
	// 		msg.ChannelID,
	// 		"Thanks for checking in. I'm still a piece of garbage",
	// 	)
	// 	if errd != nil {
	// 		fmt.Println(errd)
	// 	}

	// } else {
	// 	//user doesnt exist in map- not active
	// 	_, err := bot.client.ChannelMessageSend(
	// 		msg.ChannelID,
	// 		"Say is not currently active for "+msg.Author.Username,
	// 	)
	// 	if err != nil {
	// 		fmt.Println(err)
	// 	}
	// 	_, errd := bot.client.ChannelMessageSend(
	// 		msg.ChannelID,
	// 		"Thanks for checking in. I'm still a piece of garbage",
	// 	)
	// 	if errd != nil {
	// 		fmt.Println(errd)
	// 	}

	// }
}

func (bot Bot) onAttach(s *dg.Session, attmsg *dg.MessageAttachment, msg *dg.MessageCreate) {
	//checks to see if attachment message contains text/a title
	// if msg.Content != "" {
	// 	//posts message content and url to other channel
	// 	message, err := bot.client.ChannelMessageSend(
	// 		bot.allVars.m[msg.Author.Username],
	// 		msg.Content+" "+attmsg.URL,
	// 	)
	// 	if err != nil {
	// 		fmt.Println(err)
	// 	}
	// 	//record message id that was posted to other channel
	// 	bot.allVars.dm[bot.allVars.m[msg.Author.Username]] = message.ID
	// } else {
	// 	//message doesnt contain content
	// 	message, err := bot.client.ChannelMessageSend(bot.allVars.m[msg.Author.Username], attmsg.URL)
	// 	if err != nil {
	// 		fmt.Println(err)
	// 	}
	// 	bot.allVars.dm[bot.allVars.m[msg.Author.Username]] = message.ID
	// }
}
