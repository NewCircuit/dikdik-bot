package internal

import (
	dg "github.com/bwmarrin/discordgo"
	"strings"
)

func (bot *Bot) onMessage(s *dg.Session, msg *dg.MessageCreate) {
	if msg.Author.Bot || len(msg.GuildID) == 0 {
		return
	}

	if !strings.HasPrefix(msg.Content, bot.config.Prefix) {
		channelMap, isOK := bot.channels[msg.Author.ID]

		if isOK && channelMap.from == msg.ChannelID {
			bot.relayMessage(msg, channelMap)
		}

		return
	}

	body := strings.ToLower(msg.Content[len(bot.config.Prefix):])
	args := strings.Split(body, " ")

	switch args[0] {
	case "talk":
		bot.onSet(msg, args[1:])
	case "stop":
		bot.onUnset(msg)
	case "joke":
		bot.onJoke(msg, args[1:])
	case "fact":
		bot.onFact(msg, args[1:])
	case "help":
		bot.onHelp(msg)
	case "status":
		bot.onStatus(msg)
	default:
		break
	}
}

func (bot Bot) onEdit(_ *dg.Session, msg *dg.MessageUpdate) {
	if msg.Author != nil {
		channelMap, isOK := bot.channels[msg.Author.ID]

		if isOK {
			editing, isOK := channelMap.messages[msg.ID]

			if !isOK {
				return
			}
			bot.client.ChannelMessageEdit(channelMap.to, editing, msg.Content)
		}
	}
}

func (bot Bot) onDelete(_ *dg.Session, msgDel *dg.MessageDelete) {
	msg := msgDel.BeforeDelete
	if msg != nil {
		channelMap, isOK := bot.channels[msg.Author.ID]

		if isOK {
			deleting, isOK := channelMap.messages[msg.ID]

			if !isOK {
				return
			}
			bot.client.ChannelMessageDelete(channelMap.to, deleting)
		}
	}
}
