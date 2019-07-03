const AppConfig = {
    PROTOCOL: "ws:",
    HOST: "//" + window.location.hostname,
    // HOST: "//192.168.0.18",
    PORT: ":9000"
}

const Socket = (function () {
    let instance;

    function createInstance() {
        // TODO: add +  PORT if you want to run it locally
        const socket = new WebSocket(AppConfig.PROTOCOL + AppConfig.HOST + AppConfig.PORT);
        return socket;
    }

    return {
        getInstance: function () {
            if (!instance) {
                instance = createInstance();
            }
            return instance;
        }
    };
})();

export default Socket;