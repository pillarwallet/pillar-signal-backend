# finds any variables that are set to $OTHER_VARIABLE and resolves them
# useful for aliasing variables in heroku
for i in $(env); do VAL=$(echo $i | cut -d '=' -f 2); if [[ $VAL == \$* ]]; then eval export $(echo $i | cut -d '=' -f 1)=$VAL; fi; done
