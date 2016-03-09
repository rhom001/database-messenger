#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -p $PGPORT project < $DIR/../src/create_tables.sql
psql -p $PGPORT project < $DIR/../src/create_indexes.sql
psql -p $PGPORT project < $DIR/../src/load_data.sql
#psql -p $PGPORT project166 < $DIR/../src/triggers.sql
