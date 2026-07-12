module.exports = {
  devServer: {
    port: 3102,
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
}
