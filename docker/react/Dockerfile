FROM node:20.8.1-alpine
RUN mkdir -p deploy
WORKDIR /deploy
COPY ../../react-app/ ./
RUN npm install
ENTRYPOINT ["npm", "start"]
EXPOSE 3000