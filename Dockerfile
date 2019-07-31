ARG CACHE_IMAGE=base

FROM node:10.15.2 AS base

# dumb-init
RUN curl -Lo /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.2/dumb-init_1.2.2_amd64 \
  && chmod +x /usr/local/bin/dumb-init

# Chrome
RUN curl -sSL https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
  && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' \
  && apt-get update \
  && apt-get install -y google-chrome-stable fonts-ipafont-gothic fonts-wqy-zenhei fonts-thai-tlwg fonts-kacst ttf-freefont \
  --no-install-recommends \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
# rm is so that Kaniko wont try to add tmp files to the image

WORKDIR /app

RUN groupadd -r kibana && useradd -r -g kibana kibana && mkdir /home/kibana && chown kibana:kibana /home/kibana

RUN chown kibana /app
USER kibana

# .gitignored but committed in git
RUN mkdir /app/data && mkdir /app/plugins

FROM $CACHE_IMAGE AS builder

USER kibana

COPY --chown=kibana:kibana . /app

RUN yarn kbn bootstrap --frozen-lockfile --prefer-offline \
  && rm -rf node_modules/\@elastic/nodegit/.vscode

FROM base AS final

USER kibana

COPY --from=builder /app /app

ENTRYPOINT ["/usr/local/bin/dumb-init", "--", "yarn"]
CMD ["start"]