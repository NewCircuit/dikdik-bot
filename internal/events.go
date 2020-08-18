package internal

import (
	"fmt"
	"github.com/bwmarrin/discordgo"
	"strings"
	"time"
)

func (bot *Bot) onMessage(s *discordgo.Session, msg *discordgo.MessageCreate) {
	if msg.Author.Bot {
		return
	}
	//confirm prefix is correct
	if len(msg.GuildID) == 0 {
		return
	} else if !strings.HasPrefix(msg.Content, bot.config.Prefix) &&
		!strings.HasPrefix(msg.Content, "`"+bot.config.Prefix) &&
		!strings.HasPrefix(msg.Content, "``"+bot.config.Prefix) {
		//confirms user is commenting in the correct channel
		if msg.ChannelID == bot.allVars.cm[msg.Author.Username] {
			//confirms say is active for user and posts all messages to other channel
			if _, exists := bot.allVars.m[msg.Author.Username]; exists {
				bot.onText(s, msg)
			} else {
				return
			}
		} else {
			return
		}
	} else {
		//split string
		args := strings.Split(""+msg.Content, " ")
		//used specifically on say to clean up text
		if len(args[:]) > 1 {
			if args[0] == bot.config.Prefix+"+say" {
				if len(args[:]) > 2 {
					args[2] = strings.Join(args[2:], " ")
					//makes sure all spaces are trimmed from front and back
					for i, arg := range args {
						args[i] = strings.TrimSpace(arg)
						//stops trimming so it doesnt remove spaces from arg[2]
						if args[i] == args[2] {
							break
						}
					}
				}
			}
			//confirms channel is correct if id is used instead of tagging channel
			if len(args[:]) > 1 {
				if len(args[1]) > 19 && strings.Contains(args[1], "<") {
					runes2 := []rune(args[1])
					args[1] = string(runes2[2:20])
					_, err := s.State.Channel(args[1])
					if err != nil {
						fmt.Println(args[1], err)
						// Could not find channel.
						s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
						return
					}
				} else
				{
					_, err := s.State.Channel(args[1])
					if err != nil {
						fmt.Println(args[1], err)
						// Could not find channel.
						s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
						return
					}
				}
			}
		}

		//cut the / off the first string
		runes := []rune(args[0])
		args[0] = string(runes[1:])
		//check first arg to decide how to proceed
		args[0] = strings.ToLower(args[0])
		switch args[0] {
		case "+say":
			bot.onSet(s, msg, args[:])
			break
		case "-say":
			bot.onUnset(s, msg, args[:])
		case "jokethere":
			bot.onJokeThere(s, msg, args[:])
			break
		case "factsthere":
			bot.onFactsThere(s, msg, args[:])
			break
		case "factshere":
			bot.onFactsHere(s, msg)
			break
		case "jokehere":
			bot.onJokeHere(s, msg)
			break
		case "help":
			bot.onHelp(s, msg)
			break
		case "delete":
			bot.onDelete(s, msg)
			break
		case "status":
			bot.onStatus(s, msg)
			break
		default:
			break
		}
	}
}

func (bot *Bot) onReady(s *discordgo.Session, ready *discordgo.Ready) {
	//creates say active map
	bot.allVars.m = make(map[string]string)
	//create prior message map
	bot.allVars.dm = make(map[string]string)
	//create current channel map
	bot.allVars.cm = make(map[string]string)
	//create timestamp
	bot.allVars.tm = make(map[string]time.Time)

	//confirms bot is ready
	testing := fmt.Sprintf("ready your %s\n", ready.User.Username)
	fmt.Printf(testing)
}


func (bot Bot) onEdit(s *discordgo.Session, editmsg *discordgo.MessageUpdate) {
	if _, exists := bot.allVars.m[editmsg.Author.Username]; exists {
		if editmsg.EditedTimestamp != ""{
			//edit message in other channel
			s.ChannelMessageEdit(bot.allVars.m[editmsg.Author.Username], bot.allVars.dm[bot.allVars.m[editmsg.Author.Username]], editmsg.Content)
			//set timestamp to last message sent
			bot.allVars.tm[editmsg.Author.Username] = time.Now()
			//confirms message has been edited in other channel
			s.ChannelMessageSend(editmsg.Author.Username, "The message has been edited")
		}
	}
}
