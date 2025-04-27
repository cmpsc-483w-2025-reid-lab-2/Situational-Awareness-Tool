#!/usr/bin/env bash
#
# add-mit-header.sh
# Adds SPDX MIT identifier & 2025 copyright for REID Lab 2 above the package line in .kt files

# Header to insert (no extra newline at the end)
read -r -d '' HEADER <<'EOF'
// SPDX-License-Identifier: MIT
// Copyright (c) 2025 REID Lab 2
EOF

# Find all .kt files
find . -type f -name '*.kt' | while IFS= read -r file; do
  # Skip if header already present
  if ! grep -q 'SPDX-License-Identifier: MIT' "$file"; then
    tmp="$(mktemp)"
    while IFS= read -r line; do
      if [[ $line == package* ]]; then
        # Insert header + extra blank line BEFORE package
        printf '%s\n\n' "$HEADER" >> "$tmp"
      fi
      printf '%s\n' "$line" >> "$tmp"
    done < "$file"
    mv "$tmp" "$file"
    echo "Updated: $file"
  fi
done
