import { defineRouteMiddleware, type StarlightRouteData } from '@astrojs/starlight/route-data';
import { getCollection } from 'astro:content';

type Entry = StarlightRouteData['sidebar'][number];

const badges = {
  'in-progress': { text: 'In progress', variant: 'caution' },
  planned: { text: 'Planned', variant: 'default' },
} as const;

/** Turns "/kotlin-llm-from-scratch/ch01/s1-tensor-basics/" into "ch01/s1-tensor-basics". */
function idFromHref(href: string): string {
  const base = import.meta.env.BASE_URL.replace(/\/$/, '');
  return href.replace(base, '').replace(/^\/|\/$/g, '');
}

export const onRequest = defineRouteMiddleware(async (context) => {
  const docs = await getCollection('docs');
  const status = new Map(docs.map((d) => [d.id, d.data.status]));

  const visit = (entries: Entry[]) => {
    for (const entry of entries) {
      if (entry.type === 'group') {
        visit(entry.entries);
        continue;
      }
      const s = status.get(idFromHref(entry.href));
      if (s && s !== 'done' && !entry.badge) entry.badge = { ...badges[s] };
    }
  };
  visit(context.locals.starlightRoute.sidebar);
});
