package main

import (
	"github.com/bwmarrin/discordgo"
	"math/rand"
	"strconv"
	"time"
)

//help command
func OnHelp(s *discordgo.Session, msg *discordgo.MessageCreate) {
	//build string to display help/info/commands
	embed := buildEmbed(config.CommandTitle, config.Commands)
	s.ChannelMessageSendEmbed(msg.ChannelID, &embed)
}

//jokeThere command
func OnJokeThere(s *discordgo.Session, msg *discordgo.MessageCreate, arg []string) {
	//confirm channel ID exists
	if len(arg[:]) > 1 {
		rand.Seed(time.Now().UnixNano())
		randomIndex := rand.Intn(len(jokes))
		s.ChannelMessageSend(arg[1], jokes[randomIndex])
	} else {
		s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
	}
}

//jokeHere command
func OnJokeHere(s *discordgo.Session, msg *discordgo.MessageCreate) {
	//creates a random seed
	rand.Seed(time.Now().UnixNano())
	randomIndex := rand.Intn(len(jokes))
	s.ChannelMessageSend(msg.ChannelID, jokes[randomIndex])
}

//factsThere command
func OnFactsThere(s *discordgo.Session, msg *discordgo.MessageCreate, arg []string) {
	//confirm channel ID exists
	if len(arg[:]) > 1 {
		rand.Seed(time.Now().UnixNano())
		randomIndex := rand.Intn(len(facts))
		s.ChannelMessageSend(arg[1], facts[randomIndex])
	} else {
		s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
	}
}

//factsHere command
func OnFactsHere(s *discordgo.Session, msg *discordgo.MessageCreate) {
	//creates a random seed
	rand.Seed(time.Now().UnixNano())
	randomIndex := rand.Intn(len(facts))
	s.ChannelMessageSend(msg.ChannelID, facts[randomIndex])
}

//+say command
func OnSet(s *discordgo.Session, msg *discordgo.MessageCreate, arg []string) {
	//set the channel topic
	//turns out there is a long cool down on this so I cant use this
	//s.ChannelEditComplex(msg.ChannelID, &discordgo.ChannelEdit{
	//	Topic: ":red_circle:  Currently sending all messages to " + arg[1],
	//})
	//confirms there is a channel in the message
	if len(arg[:]) > 1 {
		//create map record of author and channel id where message is sent
		m[msg.Author.Username] = arg[1]
		//create map record of author and channel id where message is from
		cm[msg.Author.Username] = msg.ChannelID
		//set timestamp to last message sent
		tm[msg.Author.Username] = time.Now()
		//confirm there is something to post after setting the say
		if len(arg[:]) > 2 {
			//post in other channel
			message, _ := s.ChannelMessageSend(arg[1], arg[2])
			//record message id that was posted to other channel
			dm[m[msg.Author.Username]] = message.ID
			s.ChannelMessageSend(msg.ChannelID, "You are now sending messages to <#"+arg[1]+">")
		} else {
			s.ChannelMessageSend(msg.ChannelID, "You are currently sending messages to <#"+m[msg.Author.Username]+">")
		}
	} else {
		s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
	}
}

//text while say command is active
func OnText(s *discordgo.Session, msg *discordgo.MessageCreate) {

	if _, exists := tm[msg.Author.Username]; exists {
		//sets current time
		currentTime := time.Now()
		//sets old time based on info in map
		oldtime := tm[msg.Author.Username]
		//finds the difference between the two times
		diff := currentTime.Sub(oldtime)
		//confirms it hasnt been more then x# of minutes since last message
		if diff.Minutes() < sayoffTime {
			//check if there is an attachment
			if len(msg.Attachments) == 0 {
				//check if message contains content
				if msg.Content != "" {
					//posts all messages to other channels
					message, _ := s.ChannelMessageSend(m[msg.Author.Username], msg.Content)
					//record message id that was posted to other channel
					dm[m[msg.Author.Username]] = message.ID
				}
			} else {
				//msg contains an attachment
				OnAttach(s, msg.Attachments[0], msg)
			}
		} else {
			//if its been longer then x# of minutes delete records
			s.ChannelMessageSend(msg.ChannelID, " Say automatically turned off for user "+msg.Author.Username+" after "+strconv.FormatFloat(sayoffTime, 'f', 0, 64)+" minutes")
			delete(dm, m[msg.Author.Username])
			delete(m, msg.Author.Username)
			delete(tm, msg.Author.Username)
			delete(cm, msg.Author.Username)
			return
		}
	} else {
		return
	}
}

//-say command
func OnUnset(s *discordgo.Session, msg *discordgo.MessageCreate, arg []string) {
	//clear the channel topic
	//turns out there is a long cool down on this so I cant use this
	//s.ChannelEditComplex(msg.ChannelID, &discordgo.ChannelEdit{
	//	Topic: "",
	//})
	if _, exists := m[msg.Author.Username]; exists {
		//if user exists delete record in map
		s.ChannelMessageSend(msg.ChannelID, "You are no longer sending messages to channel <#"+m[msg.Author.Username]+">")
		//clear all maps of user data
		delete(dm, m[msg.Author.Username])
		delete(m, msg.Author.Username)
		delete(tm, msg.Author.Username)
		delete(cm, msg.Author.Username)
	} else {
		//if user doesnt exist return
		s.ChannelMessageSend(msg.ChannelID, "Say is not currently active for "+msg.Author.Username)
	}
}

//deletes message last posted in channel
func OnDelete(s *discordgo.Session, msg *discordgo.MessageCreate) {
	//checks if user exists- last message contains a value
	if _, exists := dm[m[msg.Author.Username]]; exists {
		s.ChannelMessageDelete(m[msg.Author.Username], dm[m[msg.Author.Username]])
		s.ChannelMessageSend(msg.ChannelID, "The message has been deleted")
	} else {
		//if user doesnt exist return
		s.ChannelMessageSend(msg.ChannelID, "There is no prior message available to be deleted")
	}
}

//checks if say is active
func OnStatus(s *discordgo.Session, msg *discordgo.MessageCreate) {
	//if user exists in map say active
	if _, exists := m[msg.Author.Username]; exists {
		s.ChannelMessageSend(msg.ChannelID, "Say is currently active for "+msg.Author.Username+" in channel <#"+m[msg.Author.Username]+">")
		s.ChannelMessageSend(msg.ChannelID, "Thanks for checking in. I'm still a piece of garbage")
	} else {
		//user doesnt exist in map- not active
		s.ChannelMessageSend(msg.ChannelID, "Say is not currently active for "+msg.Author.Username)
		s.ChannelMessageSend(msg.ChannelID, "Thanks for checking in. I'm still a piece of garbage")
	}
}

func OnEdit(s *discordgo.Session, editmsg *discordgo.MessageUpdate) {

	if _, exists := m[editmsg.Author.Username]; exists {
		if editmsg.EditedTimestamp != ""{
			//edit message in other channel
			s.ChannelMessageEdit(m[editmsg.Author.Username], dm[m[editmsg.Author.Username]], editmsg.Content)
			//set timestamp to last message sent
			tm[editmsg.Author.Username] = time.Now()
			//confirms message has been edited in other channel
			s.ChannelMessageSend(editmsg.Author.Username, "The message has been edited")
		}
	}
}

func OnAttach(s *discordgo.Session, attmsg *discordgo.MessageAttachment, msg *discordgo.MessageCreate) {
	//checks to see if attachment message contains text/a title
	if msg.Content != "" {
		//posts message content and url to other channel
		message, _ := s.ChannelMessageSend(m[msg.Author.Username], msg.Content+" "+attmsg.URL)
		//record message id that was posted to other channel
		dm[m[msg.Author.Username]] = message.ID
	} else {
		//message doesnt contain content
		message, _ := s.ChannelMessageSend(m[msg.Author.Username], attmsg.URL)
		dm[m[msg.Author.Username]] = message.ID
	}
}
