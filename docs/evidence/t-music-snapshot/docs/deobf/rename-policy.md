# Rename policy

- Rename a class/method/field only when you understand what it does.
- Prefer short, accurate semantic names over guessed original names.
- Keep the canonical smali conservative — one change at a time.
- Put human-reviewed names in `mappings/manual-enigma/` using Enigma format.
- Let JADX keep its own auto-generated aliases in `mappings/jadx/`.
- For broken JADX methods, document the smali path instead of inventing Java.
