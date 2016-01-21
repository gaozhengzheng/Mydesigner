//#include<stdio.h>


int main()
{
  int  i=0,n,nul=1;

  scanf("%d",&n);
  i++;
  if(n<=0){
	  printf("%d",n);
  }else{
	  while(i<=n){
		  nul=nul*i;
		  i++;
	  }
	  printf("%d",nul);
  }

   return 0;
 
}
