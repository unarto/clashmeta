package tunnel

import (
    C "github.com/metacubex/mihomo/constant"
    "github.com/metacubex/mihomo/tunnel/statistic"
)

func CloseAllConnections() {
    statistic.DefaultManager.Range(func(c statistic.Tracker) bool {
        if conn, ok := c.(C.Conn); ok {
            conn.Close()
        }
        return true
    })
}

func closeMatch(filter func(conn C.Conn) bool) {
    statistic.DefaultManager.Range(func(c statistic.Tracker) bool {
        if conn, ok := c.(C.Conn); ok {
            if filter(conn) {
                conn.Close()
            }
        }
        return true
    })
}

func closeConnByGroup(name string) {
    closeMatch(func(conn C.Conn) bool {
        for _, c := range conn.Chains() {
            if c == name {
                return true
            }
        }
        return false
    })
}
