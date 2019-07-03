import React from 'react';
import Socket from './Socket';

class App extends React.Component {
  constructor() {
    super();

    this.state = {
      rpm: 0,
      speed: 50,
      isMotorStarted: false,
      direction: -1,
    }

    this.handleSwitch = (e) => {
      const data = { isMotorStarted: e.target.checked }
      this.setState(data);
      this.sendNewData(data);
    }

    this.handleSpeed = (e) => {
      const data = { speed: parseInt(e.target.value) };
      this.setState(data);
      this.sendNewData(data);
    }

    this.handleDirection = (e) => {
      const data = { direction: e.target.checked ? 1 : -1 };
      this.setState(data);
      this.sendNewData(data);
    }

    this.changeSpeed = (e) => {
      let newSpeed;
      switch (e.target.name) {
        case "plus":
          newSpeed = this.state.speed + 1 <= 100 ? this.state.speed + 1 : 100;
          break;
        case "minus":
          newSpeed = this.state.speed - 1 >= 1 ? this.state.speed - 1 : 1;
          break;

        default:
          break;
      }
      const data = { speed: newSpeed };
      this.setState(data);
      this.sendNewData(data);
    }

    this.sendNewData = (data) => {
      if (this.socket) {
        try {
          let messageDto = JSON.stringify({
            type: "DATA_CHANGED",
            data: JSON.stringify({
              speed: parseInt(this.state.speed),
              isMotorStarted: this.state.isMotorStarted,
              direction: this.state.direction,
              ...data
            })
          });
          // console.log(messageDto);
          this.socket.send(messageDto);
        } catch (error) {
          console.log("Not Connected.")
        }
      }
    }

    this.registerSocket = () => {
      this.socket = Socket.getInstance();

      this.socket.onmessage = (response) => {
        let message = JSON.parse(response.data);
        let data = JSON.parse(message.data);

        // console.log(data);

        switch (message.type) {
          case "DATA_CHANGED":
            this.setState({
              speed: data.speed,
              isMotorStarted: JSON.parse(data.isMotorStarted),
              direction: data.direction,
            })
            break;
          case "RPM":
            this.setState({
              rpm: data
            })
            break;
          default:
        }
      }

      this.socket.onopen = () => {
        let messageDto = JSON.stringify({ type: "USER_JOIN" });
        this.socket.send(messageDto);
      }

      window.onbeforeunload = () => {
        let messageDto = JSON.stringify({ type: "USER_DISCONECT" });
        this.socket.send(messageDto);
      }
    }

    this.calculateSpeed = () => {
      return Math.round(60000 / 32 / (102 - this.state.speed));
    }
  }

  componentDidMount() {
    this.registerSocket();
  }

  render() {
    return (
      <div className="app" >
        <header>
          <div className="title">Stepper UI</div>
        </header>
        <main>
          <div className="card">
            <p className="speed-viewer">Currnet Speed: <span className="widget green">{this.state.isMotorStarted ? this.state.rpm : 0}</span>rpm</p>
          </div>
          <div className="card">
            <h2>Speed control</h2>
            <div className="speed-panel">
              <h3>Default Speed:</h3>
              <div className="number-input">
                <button name="minus" onClick={this.changeSpeed} ></button>
                <div className="quantity">{this.calculateSpeed()} rpm</div>
                <button name="plus" onClick={this.changeSpeed} className="plus"></button>
              </div>
            </div>

            <input type="range" min="1" max="100" value={this.state.speed} onChange={this.handleSpeed} />

            <div className="mid">
              <h3>Direction:</h3>
              <label className="rocker">
                <input type="checkbox" checked={this.state.direction === 1} onChange={this.handleDirection} />
                <span className="switch-left">left</span>
                <span className="switch-right">right</span>
              </label>
            </div>

            <div className="mid">
              <h3>Motor:</h3>
              <label className="rocker rocker-small">
                <input type="checkbox" checked={this.state.isMotorStarted} onChange={this.handleSwitch} />
                <span className="switch-left">On</span>
                <span className="switch-right">Off</span>
              </label>
            </div>
          </div>

        </main>
      </div>
    );
  }
}

export default App;
