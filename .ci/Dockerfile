ARG NODE_VERSION=10.15.2

FROM node:${NODE_VERSION} AS base

# dumb-init
# RUN curl -Lo /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.2/dumb-init_1.2.2_amd64 \
#   && chmod +x /usr/local/bin/dumb-init

# Chrome
RUN curl -sSL https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
  && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' \
  && apt-get update \
  && apt-get install -y jq google-chrome-stable fonts-ipafont-gothic fonts-wqy-zenhei fonts-thai-tlwg fonts-kacst ttf-freefont \
  --no-install-recommends
# rm is so that Kaniko wont try to add tmp files to the image

RUN LATEST_VAULT_RELEASE=$(curl -s https://api.github.com/repos/hashicorp/vault/tags | jq --raw-output .[0].name[1:]) \
  && curl -L https://releases.hashicorp.com/vault/${LATEST_VAULT_RELEASE}/vault_${LATEST_VAULT_RELEASE}_linux_amd64.zip -o vault.zip \
  && unzip vault.zip \
  && rm vault.zip \
  && chmod +x vault \
  && mv vault /usr/local/bin/vault

# WORKDIR /app

RUN groupadd -r kibana && useradd -r -g kibana kibana && mkdir /home/kibana && chown kibana:kibana /home/kibana

COPY .ci/bash_standard_lib.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/bash_standard_lib.sh

# RUN chown kibana /app
USER kibana

