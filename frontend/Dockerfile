FROM node:22.14-alpine

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# In dev you might want `npm start`, but for production:
CMD ["npx","serve","-s","build","-l","3000"]
