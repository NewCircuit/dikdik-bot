package internal

import (
	"fmt"
	dg "github.com/bwmarrin/discordgo"
	"strings"
	"time"
)

func (bot *Bot) onMessage(s *dg.Session, msg *dg.MessageCreate) {
	if msg.Author.Bot || len(msg.GuildID) == 0 {
		return
	}
	//confirm prefix is correct
	if !strings.HasPrefix(msg.Content, bot.config.Prefix) {
		//confirms user is commenting in the correct channel
		if msg.ChannelID == bot.allVars.cm[msg.Author.Username] {
			//confirms say is active for user and posts all messages to other channel
			if err, exists := bot.allVars.m[msg.Author.Username]; exists {
				if err != "" {
					fmt.Println(err)
				}
				bot.onText(s, msg)
			}
		}
		return
	}

	//split string
	body := strings.ToLower(msg.Content[len(bot.config.Prefix):])
	args := strings.Split(body, " ")

	switch args[0] {
	case "+say":
		bot.onSet(s, msg, args)
		break
	case "-say":
		bot.onUnset(s, msg, args)
	case "joke":
		bot.onJoke(s, msg, args)
		break
	case "factsthere":
		bot.onFactsThere(s, msg, args)
		break
	case "factshere":
		bot.onFactsHere(s, msg)
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

func (bot Bot) onEdit(s *dg.Session, editmsg *dg.MessageUpdate) {
	if editmsg.EditedTimestamp != "" {
		if _, exists := bot.allVars.m[editmsg.Author.Username]; exists {

			//edit message in other channel
			_, err := s.ChannelMessageEdit(
				bot.allVars.m[editmsg.Author.Username],
				bot.allVars.dm[bot.allVars.m[editmsg.Author.Username]],
				editmsg.Content,
			)
			if err != nil {
				fmt.Println(err)
			}

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
