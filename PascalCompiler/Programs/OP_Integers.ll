; ModuleID = 'MiniPascal'
source_filename = "C:\Users\Victo\OneDrive\Documents\- UNITEC\Ing. Sistemas\2025 - Periodo 1\Compiladores 1\Proyect 2\Proyecto2Compi1_MiniPascalCompiler\PascalCompiler\Programs\OP_Integers.txt"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; ModuleID = 'MiniPascal'
source_filename = "output.ll"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str._t0649c8ee_8c75_4259_86ea_b7f8b5556b9d = private unnamed_addr constant [4 x i8] c"P: \00"

@.str._t10b76b53_8e1c_4327_98e6_8ea4ed143106 = private unnamed_addr constant [4 x i8] c"Q: \00"

@.str._te6570514_3995_4750_a127_b671cd8a2533 = private unnamed_addr constant [4 x i8] c"R: \00"

@.str._t6a15cc39_ee1d_4810_a2a1_8d0236ef4ecb = private unnamed_addr constant [4 x i8] c"S: \00"

@.str._tb30d88c8_77a5_4cf9_b5ec_1701c822a2c6 = private unnamed_addr constant [11 x i8] c"Division: \00"

%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }
@str_fmt = unnamed_addr constant [4 x i8] c"%d\0A\00"
@stdin = external global %struct._IO_FILE*
@double_fmt = private unnamed_addr constant [4 x i8] c"%f\0A\00"

define i32 @main() {
br label %OperacionesEnteros
OperacionesEnteros:
%p = alloca i32
store i32 0, i32* %p
%q = alloca i32
store i32 0, i32* %q
%r = alloca i32
store i32 0, i32* %r
%s = alloca i32
store i32 0, i32* %s
%division = alloca i32
store i32 0, i32* %division
%_t0 = alloca i32
store i32 200, i32* %_t0
store i32 200, i32* %p
%_t1 = alloca i32
store i32 25, i32* %_t1
store i32 25, i32* %q
%_t2 = alloca i32
store i32 8, i32* %_t2
store i32 8, i32* %r
call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t0649c8ee_8c75_4259_86ea_b7f8b5556b9d, i32 0, i32 0))

%_t4 = alloca i32
store i32 200, i32* %_t4
%_t87f4ed00_4cbb_422d_a1a4_7aee230ad3e8 = load i32, i32* %p
call void @write_int(i32 %_t87f4ed00_4cbb_422d_a1a4_7aee230ad3e8)

call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t10b76b53_8e1c_4327_98e6_8ea4ed143106, i32 0, i32 0))

%_t6 = alloca i32
store i32 25, i32* %_t6
%_t43fd6026_eac8_43ce_a642_92cc49850418 = load i32, i32* %q
call void @write_int(i32 %_t43fd6026_eac8_43ce_a642_92cc49850418)

call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._te6570514_3995_4750_a127_b671cd8a2533, i32 0, i32 0))

%_t8 = alloca i32
store i32 8, i32* %_t8
%_t6834a465_77ce_47b4_a442_eb7ba55fb9cc = load i32, i32* %r
call void @write_int(i32 %_t6834a465_77ce_47b4_a442_eb7ba55fb9cc)

%_t9 = alloca i32
store i32 4, i32* %_t9
store i32 4, i32* %s
call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t6a15cc39_ee1d_4810_a2a1_8d0236ef4ecb, i32 0, i32 0))

%_t11 = alloca i32
store i32 4, i32* %_t11
%_t91f4830a_d4c4_4dff_99dc_ce553b64539d = load i32, i32* %s
call void @write_int(i32 %_t91f4830a_d4c4_4dff_99dc_ce553b64539d)

%_t12 = alloca i32
store i32 200, i32* %_t12
%_t13 = alloca i32
store i32 4, i32* %_t13
%_t14 = call i32 @DIV( i32 p, i32 s)
store i32 %_t14_val, i32* %division
call void @write_string(i8* getelementptr inbounds ([11 x i8], [11 x i8]* @.str._tb30d88c8_77a5_4cf9_b5ec_1701c822a2c6, i32 0, i32 0))

%_t16 = alloca i32
store i32 %_t14_val, i32* %_t16
call void @write_int(i32 %_t14_val)

ret i32 0
}
define void @write_double(double %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @double_fmt, i32 0, i32 0), double %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_int(i32 %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @str_fmt, i32 0, i32 0), i32 %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_string(i8* %str) {
  %str_ptr = alloca i8*
  store i8* %str, i8** %str_ptr
  %str_val = load i8*, i8** %str_ptr
  call i32 @puts(i8* %str_val)
  ret void
}
define i32 @read() {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  %stdin_val = load %struct._IO_FILE*, %struct._IO_FILE** @stdin
  %result = call i8* @fgets(i8* %buf_ptr, i32 32, %struct._IO_FILE* %stdin_val)
  %num = call i32 @atoi(i8* %buf_ptr)
  ret i32 %num
}
declare i32 @atoi(i8*)
declare i32 @sprintf(i8*, i8*, ...)
declare i32 @puts(i8*)
declare i8* @fgets(i8*, i32, %struct._IO_FILE*)
declare void @exit(i32)

; Function Attrs: noinline nounwind optnone uwtable
declare i32 @printf(i8*, ...) #0

attributes #0 = { noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 10.0.0-4ubuntu1 "}
