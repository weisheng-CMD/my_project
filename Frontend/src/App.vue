<template>
  <router-view />
</template>

<style>
:root {
  --primary: #4f46e5;
  --danger: #dc2626;
  --success: #16a34a;
  --warning: #ea580c;
  --bg: #f1f5f9;
  --card: #fff;
  --border: #e2e8f0;
  --text: #1e293b;
  --muted: #64748b;
}
* { margin: 0; padding: 0; box-sizing: border-box; }
body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  background: var(--bg);
  color: var(--text);
}
</style>
