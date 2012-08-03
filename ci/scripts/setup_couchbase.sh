#!/bin/sh

sudo service memcached stop

sudo cp ./ci/conf/couchbase.conf /etc/security/limits.d/
sudo chmod 644 /etc/security/limits.d/couchbase.conf
wget -q $CB_URL
sudo dpkg -i $CB_DEB
sudo chmod a+x ./ci/scripts/cluster_connect
./ci/scripts/cluster_connect -n 1
$CB_CONFIG_CMD
