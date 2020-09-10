package internal

import (
	"fmt"
	util "github.com/Floor-Gang/utilpkg/botutil"
	dg "github.com/bwmarrin/discordgo"
	"math/rand"
	"time"
)

//help command
func (bot Bot) onHelp(s *dg.Session, msg *dg.MessageCreate) {
	//build string to display help/info/commands
	embed := bot.buildEmbed()
	_, err := s.ChannelMessageSendEmbed(msg.ChannelID, &embed)
	if err != nil {
		fmt.Println(err)
	}
}

//jokeHere command
func (bot Bot) onJoke(s *dg.Session, msg *dg.MessageCreate, args []string) {
	//creates a random seed
	rand.Seed(time.Now().UnixNano())
	randomIndex := rand.Intn(len(bot.jokes))
	var channel string

	if len(args) > 1 {
		channel = util.FilterTag(args[1])
	} else {
		channel = msg.ChannelID
	}

	_, err := s.ChannelMessageSend(channel, bot.jokes[randomIndex])
	if err != nil {
		fmt.Println(err)
	}
}

//factsThere command
func (bot Bot) onFactsThere(s *dg.Session, msg *dg.MessageCreate, arg []string) {
	//confirm channel ID exists
	if len(arg[:]) > 1 {
		rand.Seed(time.Now().UnixNano())
		randomIndex := rand.Intn(len(bot.facts))
		_, err := s.ChannelMessageSend(arg[1], bot.facts[randomIndex])
		if err != nil {
			fmt.Println(err)
		}
	} else {
		_, err := s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
		if err != nil {
			fmt.Println(err)
		}
	}
}

//factsHere command
func (bot Bot) onFactsHere(s *dg.Session, msg *dg.MessageCreate) {
	//creates a random seed
	rand.Seed(time.Now().UnixNano())
	randomIndex := rand.Intn(len(bot.facts))
	_, err := s.ChannelMessageSend(msg.ChannelID, bot.facts[randomIndex])
	if err != nil {
		fmt.Println(err)
	}

}

//+say command
func (bot Bot) onSet(s *dg.Session, msg *dg.MessageCreate, args []string) {
	if len(args) > 0 {
		channelID := util.FilterTag(args[0])
		channel, err := bot.client.Channel(channelID)

		if err != nil {
			util.Reply(bot.client, msg.Message, "Couldn't find "+args[0])
			return
		}

		channelMap := &ChannelMap{
			from:     msg.ChannelID,
			to:       channelID,
			user:     msg.Author.ID,
			messages: make(map[string]string),
		}

		bot.channels[channelMap.user] = channelMap

		util.Reply(bot.client, msg.Message, "Now talking in "+channel.Mention())

	} else {
		_, err := s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
		if err != nil {
			fmt.Println(err)
		}
	}
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
	} else {
		_, _ = util.Reply(bot.client, msg.Message, "Failed to send message. Do I have permissions?")
	}
}

//-say command
func (bot Bot) onUnset(s *dg.Session, msg *dg.MessageCreate, arg []string) {
	//clear the channel topic
	//turns out there is a long cool down on this so I cant use this
	//s.ChannelEditComplex(msg.ChannelID, &discordgo.ChannelEdit{
	//	Topic: "",
	//})
	if err, exists := bot.allVars.m[msg.Author.Username]; exists {
		if err != "" {
			fmt.Println(err)
		}
		//if user exists delete record in map
		_, err := s.ChannelMessageSend(
			msg.ChannelID,
			"You are no longer sending messages to channel "+
				"<#"+bot.allVars.m[msg.Author.Username]+">",
		)
		if err != nil {
			fmt.Println(err)
		}

		//clear all maps of user data
		delete(bot.allVars.dm, bot.allVars.m[msg.Author.Username])
		delete(bot.allVars.m, msg.Author.Username)
		delete(bot.allVars.tm, msg.Author.Username)
		delete(bot.allVars.cm, msg.Author.Username)
	} else {
		//if user doesnt exist return
		_, err := s.ChannelMessageSend(
			msg.ChannelID,
			"Say is not currently active for "+msg.Author.Username,
		)
		if err != nil {
			fmt.Println(err)
		}
	}
}

//deletes message last posted in channel
func (bot Bot) onDelete(s *dg.Session, msg *dg.MessageCreate) {
	//checks if user exists- last message contains a value
	if _, exists := bot.allVars.dm[bot.allVars.m[msg.Author.Username]]; exists {
		s.ChannelMessageDelete(
			bot.allVars.m[msg.Author.Username],
			bot.allVars.dm[bot.allVars.m[msg.Author.Username]],
		)
		s.ChannelMessageSend(msg.ChannelID, "The message has been deleted")
	} else {
		//if user doesnt exist return
		s.ChannelMessageSend(msg.ChannelID, "There is no prior message available to be deleted")
	}
}

//checks if say is active
func (bot Bot) onStatus(s *dg.Session, msg *dg.MessageCreate) {
	//if user exists in map say active
	if err, exists := bot.allVars.m[msg.Author.Username]; exists {
		if err != "" {
			fmt.Println(err)
		}
		_, err := s.ChannelMessageSend(
			msg.ChannelID,
			"Say is currently active for "+msg.Author.Username+" in channel "+
				"<#"+bot.allVars.m[msg.Author.Username]+">",
		)
		if err != nil {
			fmt.Println(err)
		}
		_, errd := s.ChannelMessageSend(
			msg.ChannelID,
			"Thanks for checking in. I'm still a piece of garbage",
		)
		if errd != nil {
			fmt.Println(errd)
		}

	} else {
		//user doesnt exist in map- not active
		_, err := s.ChannelMessageSend(
			msg.ChannelID,
			"Say is not currently active for "+msg.Author.Username,
		)
		if err != nil {
			fmt.Println(err)
		}
		_, errd := s.ChannelMessageSend(
			msg.ChannelID,
			"Thanks for checking in. I'm still a piece of garbage",
		)
		if errd != nil {
			fmt.Println(errd)
		}

	}
}

func (bot Bot) onAttach(s *dg.Session, attmsg *dg.MessageAttachment, msg *dg.MessageCreate) {
	//checks to see if attachment message contains text/a title
	if msg.Content != "" {
		//posts message content and url to other channel
		message, err := s.ChannelMessageSend(
			bot.allVars.m[msg.Author.Username],
			msg.Content+" "+attmsg.URL,
		)
		if err != nil {
			fmt.Println(err)
		}
		//record message id that was posted to other channel
		bot.allVars.dm[bot.allVars.m[msg.Author.Username]] = message.ID
	} else {
		//message doesnt contain content
		message, err := s.ChannelMessageSend(bot.allVars.m[msg.Author.Username], attmsg.URL)
		if err != nil {
			fmt.Println(err)
		}
		bot.allVars.dm[bot.allVars.m[msg.Author.Username]] = message.ID
	}
}
