FROM nginx
MAINTAINER xiaoyang

RUN mkdir -p /home/shortlink
WORKDIR /home/shortlink

# 创建 SSL 目录，确保存在
RUN mkdir -p /etc/nginx/ssl

COPY ./conf/nginx.conf /etc/nginx/nginx.conf
COPY ./dist /home/shortlink/dist

# 复制证书文件 - 使用通配符证书或者复制相同证书给两个域名使用
COPY ./ssl/ywenrou.cn_bundle.crt /etc/nginx/ssl/shortlink.ywenrou.cn_bundle.crt
COPY ./ssl/ywenrou.cn.key /etc/nginx/ssl/shortlink.ywenrou.cn.key
COPY ./ssl/ywenrou.cn_bundle.crt /etc/nginx/ssl/s.ywenrou.cn_bundle.crt
COPY ./ssl/ywenrou.cn.key /etc/nginx/ssl/s.ywenrou.cn.key

# 设置正确的文件权限
RUN chmod 644 /etc/nginx/ssl/*.crt \
    && chmod 600 /etc/nginx/ssl/*.key \
    && chown -R nginx:nginx /etc/nginx/ssl/ \
    && chown -R nginx:nginx /home/shortlink/dist/ \
    && chmod 755 /home/shortlink/dist/

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]