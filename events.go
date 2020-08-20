package internal

import (
	"fmt"
	"github.com/bwmarrin/discordgo"
	"strings"
	"time"
)

<<<<<<< HEAD:DikDik-Bot/internal/events.go
func (bot *Bot) onMessage(s *discordgo.Session, msg *discordgo.MessageCreate) {
=======
func onMessage(s *discordgo.Session, msg *discordgo.MessageCreate) {
>>>>>>> f6a4cb7de86884636393f079d2b7e912d4f92231:events.go
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
<<<<<<< HEAD:DikDik-Bot/internal/events.go
			if err, exists := bot.allVars.m[msg.Author.Username]; exists {
				if err != "" {
					fmt.Println(err)
				}
				bot.onText(s, msg)
=======
			if _, exists := m[msg.Author.Username]; exists {
				onText(s, msg)
>>>>>>> f6a4cb7de86884636393f079d2b7e912d4f92231:events.go
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
						_, err := s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
						if err != nil {
							fmt.Println(err)
						}
						return
					}
				} else
				{
					_, err := s.State.Channel(args[1])
					if err != nil {
						fmt.Println(args[1], err)
						// Could not find channel.
						_, err := s.ChannelMessageSend(msg.ChannelID, "Invalid Channel. Use /help to see commands")
						if err != nil {
							fmt.Println(err)
						}
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
<<<<<<< HEAD:DikDik-Bot/internal/events.go
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
=======
			onSet(s, msg, args[:])
			break
		case "-say":
			onUnset(s, msg, args[:])
		case "jokethere":
			onJokeThere(s, msg, args[:])
			break
		case "factsthere":
			onFactsThere(s, msg, args[:])
			break
		case "factshere":
			onFactsHere(s, msg)
			break
		case "jokehere":
			onJokeHere(s, msg)
			break
		case "help":
			onHelp(s, msg)
			break
		case "delete":
			onDelete(s, msg)
			break
		case "status":
			onStatus(s, msg)
>>>>>>> f6a4cb7de86884636393f079d2b7e912d4f92231:events.go
			break
		default:
			break
		}
	}
}

<<<<<<< HEAD:DikDik-Bot/internal/events.go
func (bot Bot) onEdit(s *discordgo.Session, editmsg *discordgo.MessageUpdate) {
	if editmsg.EditedTimestamp != "" {
		if _, exists := bot.allVars.m[editmsg.Author.Username]; exists {

			//edit message in other channel
			_, err := s.ChannelMessageEdit(bot.allVars.m[editmsg.Author.Username], bot.allVars.dm[bot.allVars.m[editmsg.Author.Username]], editmsg.Content)
			if err != nil {
				fmt.Println(err)
			}
=======
func onReady(s *discordgo.Session, ready *discordgo.Ready) {
	//creates say active map
	m = make(map[string]string)
	//create prior message map
	dm = make(map[string]string)
	//create current channel map
	cm = make(map[string]string)
	//create timestamp
	tm = make(map[string]time.Time)
>>>>>>> f6a4cb7de86884636393f079d2b7e912d4f92231:events.go

			//set timestamp to last message sent
			bot.allVars.tm[editmsg.Author.Username] = time.Now()
			//confirms message has been edited in other channel
			_, errd := s.ChannelMessageSend(editmsg.Author.Username, "The message has been edited")
			if errd != nil {
				fmt.Println(errd)
			}

		}
	}
}
