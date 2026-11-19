// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

const repo = 'https://github.com/mohaqeq/kotlin-llm-from-scratch';

const chapters = [
  ['ch01', 'Ch 1 · Foundations'],
  ['ch02', 'Ch 2 · Tokenizer and data'],
  ['ch03', 'Ch 3 · Attention'],
  ['ch04', 'Ch 4 · GPT model'],
  ['ch05', 'Ch 5 · Training'],
  ['ch06', 'Ch 6 · Bonus'],
];

export default defineConfig({
  // GitHub Pages project site: https://mohaqeq.github.io/kotlin-llm-from-scratch/
  site: 'https://mohaqeq.github.io',
  base: '/kotlin-llm-from-scratch',
  integrations: [
    starlight({
      title: 'Kotlin LLM from Scratch',
      description: 'Build a GPT-style language model in pure Kotlin, one failing test at a time.',
      social: [{ icon: 'github', label: 'GitHub', href: repo }],
      editLink: { baseUrl: `${repo}/edit/main/site/` },
      customCss: [
        '@fontsource-variable/source-serif-4',
        '@fontsource-variable/jetbrains-mono',
        './src/styles/theme.css',
      ],
      // Adds the "In progress" / "Planned" badges from each page's `status` front matter.
      routeMiddleware: './src/routeData.ts',
      sidebar: [
        { label: 'Start here', items: ['how-to-learn', 'about'] },
        ...chapters.map(([dir, label]) => ({ label, items: [{ autogenerate: { directory: dir } }] })),
      ],
    }),
  ],
});
