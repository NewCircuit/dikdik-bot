package internal

import (
	util "github.com/Floor-Gang/utilpkg/config"
	"log"
	"os"
)

type DikDikConfig struct {
	Token     string `yaml:"bot_token"`
	Prefix    string `yaml:"bot_prefix"`
	JokesPath string `yaml:"jokes_path"`
	FactsPath string `yaml:"facts_path"`
}

const defaultPath = "dikdik-config.yml"

func GetConfig() (config *DikDikConfig) {
	configPath := os.Getenv("CONFIG_PATH")

	if len(configPath) == 0 {
		configPath = defaultPath
	}

	config = &DikDikConfig{
		Prefix:    "!",
		JokesPath: "./jokes.txt",
		FactsPath: "./facts.txt",
	}

	if err := util.GetConfig(configPath, config); err != nil {
		log.Fatalln(err)
	}

	return config
}
