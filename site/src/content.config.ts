import { defineCollection } from 'astro:content';
import { z } from 'astro/zod';
import { docsLoader } from '@astrojs/starlight/loaders';
import { docsSchema } from '@astrojs/starlight/schema';

export const collections = {
  docs: defineCollection({
    loader: docsLoader(),
    schema: docsSchema({
      extend: z.object({
        /**
         * Progress of the page. The sidebar shows a badge for every value
         * except "done". The landing page uses it for the chapter map.
         */
        status: z.enum(['planned', 'in-progress', 'done']).default('done'),
      }),
    }),
  }),
};
