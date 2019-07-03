const path = require('path');

module.exports = {
    entry: path.resolve('./src/index.js'),
    output: {
        path: path.resolve('./build'),
        filename: 'bundle.min.js'
    },
    devtool: '#chep-eval-source-map',
    devServer: {
        port: 3000,
        contentBase: path.resolve('./build'),
        historyApiFallback: true,
        publicPath: '/',
    },
    module: {
        rules: [
            {
                test: /\.js/,
                exclude: /node_modules/,
                loaders: 'babel-loader'
            }
        ]
    }
}