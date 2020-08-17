package main

import (
	util "github.com/Floor-Gang/utilpkg/config"
	"log"
)

type DikDikConfig struct {
	Token        string            `yaml:"bot_token"`
	Prefix       string            `yaml:"bot_prefix"`
	CommandTitle string            `yaml:"command_title"`
	Commands     []string          `yaml:"commands"`
}

const configPath = "./dikdik-config.yml"

func GetConfig() DikDikConfig {
	//err := util.GetConfig(configPath, &DikDikConfig)
	//if err != nil {
	//	log.Fatalln(err)
	//}


	//return defaultConfig
}

func (config DikDikConfig) Save() {
	util.Save(configPath, &config)
}
