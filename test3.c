//#include<stdio.h>

/*1111*/

int main()
{
	int i,sum=1,n;
	scanf("%d",&n);
	if(n<=0){
		printf("%d",n);
		
	}else{
		for(i=1;i<=n;i++){
			sum=sum*i;
		}
	}
	return 0;
 } 
