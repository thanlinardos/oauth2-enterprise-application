import { EnvironmentPlugin } from 'webpack';    // NOSONAR typescript:S1128
const Dotenv = require('dotenv-webpack');
module.exports = {
  plugins: [new Dotenv()],
};
