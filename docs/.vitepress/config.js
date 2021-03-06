module.exports = {
  lang: 'en-US',
  title: 'Elaina',
  description: 'The ultimate personal assistant chatbot.',

  themeConfig: {
    repo: 'lirc572/ip',
    docsDir: 'docs',

    editLinks: true,
    editLinkText: 'Edit this page on GitHub',
    lastUpdated: 'Last Updated',

    nav: [
      {
        text: 'Guides',
        link: '/pages/introduction',
        activeMatch: '^/$|^/pages/'
      },
      {
        text: 'Releases',
        link: 'https://github.com/lirc572/ip/releases'
      }
    ],

    sidebar: {
      '/': [
        {
          text: 'Introduction',
          children: [
            { text: 'What is Elaina?', link: '/pages/introduction' },
            { text: 'Getting Started', link: '/pages/getting-started' }
          ]
        },
        {
          text: 'Guides',
          children: [
            { text: 'User Guide', link: '/pages/user-guide' },
            { text: 'Developer Guide', link: '/pages/developer-guide' }
          ]
        },
        {
          text: 'Download',
          link: '/pages/download'
        },
        {
          text: 'Troubleshooting',
          link: '/pages/troubleshooting'
        }
      ]
    }
  }
}