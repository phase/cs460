client-c: client.c client.h threadpool.c
	cc -lpthread ./client.c ./threadpool.c -o client-c
	./client-c

t-server-c: t_server.c t_server.h threadpool.c
	cc -lpthread ./t_server.c ./threadpool.c -o t-server-c
	./t-server-c

s-server-c: s_server.c s_server.h threadpool.c
	cc -lpthread ./s_server.c ./threadpool.c -o s-server-c
	./s-server-c

m-server-c: m_server.c m_server.h threadpool.c
	cc -lpthread ./m_server.c ./threadpool.c -o m-server-c
	./m-server-c
