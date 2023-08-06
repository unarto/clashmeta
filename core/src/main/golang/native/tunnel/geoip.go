package tunnel

import (
	"fmt"

	"github.com/oschwald/maxminddb-golang"

	"github.com/Dreamacro/clash/component/mmdb"
)

func InstallSideloadGeoip(block []byte) error {
	if block == nil {
		mmdb.InstallOverride(nil)

		return nil
	}

	db, err := maxminddb.FromBytes(block)
	if err != nil {
		return fmt.Errorf("load sideload geoip mmdb: %s", err.Error())
	}

	mmdb.InstallOverride(db)

	return nil
}
