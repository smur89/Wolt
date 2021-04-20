FROM openjdk:11-jre-slim

COPY target/universal/stage /server
RUN chmod +x /server/bin/wolt

LABEL \
  org.label-schema.vendor="smur89" \
  org.label-schema.name="wolt" \
  org.label-schema.description="wolt challenge" \
  maintainer="@smur89"

# Install curl for HEALTHCHECK
RUN apt-get update -y \
  && apt-get install --no-install-recommends -y curl \
  && apt-get install --no-install-recommends -y openssh-client \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /server

# Set default JAVA_OPTS
ENV JAVA_OPTS \
  -XX:+UseContainerSupport \
  -XshowSettings:vm

# HEALTHCHECK
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -sf localhost:3000/healthz || exit 1

EXPOSE 3000
CMD ["/server/bin/wolt"]
