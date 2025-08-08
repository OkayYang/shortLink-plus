FROM nginx
MAINTAINER xiaoyang

RUN mkdir -p /home/shortlink
WORKDIR /home/shortlink

# 创建 SSL 目录，确保存在
RUN mkdir -p /etc/nginx/ssl

COPY ./conf/nginx.conf /etc/nginx/nginx.conf
COPY ./dist /home/shortlink/dist

COPY ./ssl/ywenrou.cn_bundle.crt /etc/nginx/ssl/ywenrou.cn_bundle.crt
COPY ./ssl/ywenrou.cn.key /etc/nginx/ssl/ywenrou.cn.key
COPY ./ssl/ywenrou.cn_bundle.pem /etc/nginx/ssl/ywenrou.cn_bundle.pem

RUN chmod 644 /etc/nginx/ssl/ywenrou.cn_bundle.pem \
    && chmod 644 /etc/nginx/ssl/ywenrou.cn.key \
    && chmod 644 /etc/nginx/ssl/ywenrou.cn_bundle.crt
