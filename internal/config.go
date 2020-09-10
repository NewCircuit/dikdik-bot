package internal

import (
	util "github.com/Floor-Gang/utilpkg/config"
	"log"
	"os"
)

type DikDikConfig struct {
	Token        string `yaml:"bot_token"`
	Prefix       string `yaml:"bot_prefix"`
	CSVPathJokes string `yaml:"csv_path_jokes"`
	CSVPathFacts string `yaml:"csv_path_facts"`
}

const defaultPath = "dikdik-config.yml"

func GetConfig() (config DikDikConfig) {
	configPath := os.Getenv("CONFIG_PATH")

	if len(configPath) == 0 {
		configPath = defaultPath
	}

	config = DikDikConfig{
		Prefix: "/",
	}

	err := util.GetConfig(configPath, &config)
	if err != nil {
		log.Fatalln(err)
	}

	return config
}
