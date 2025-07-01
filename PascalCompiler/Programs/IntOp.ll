; ModuleID = 'MiniPascal'
source_filename = "output.ll"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str._t8fe02a3e_deb4_458d_af59_b492c74da090 = private unnamed_addr constant [4 x i8] c"X: \00"

@.str._td5a85f85_0108_4eb3_98a0_e8a4e3626df9 = private unnamed_addr constant [4 x i8] c"Y: \00"

@.str._t47c512e0_5c6d_430c_b2c9_e7ca533c85d7 = private unnamed_addr constant [4 x i8] c"Z: \00"

@.str._t96561801_b47b_4899_a5af_9a1c3ae95c5b = private unnamed_addr constant [4 x i8] c"A: \00"

@.str._t31f53cc9_91ca_4b13_af70_be6d74bb3d18 = private unnamed_addr constant [7 x i8] c"Mult: \00"

%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }
@str_fmt = unnamed_addr constant [4 x i8] c"%d\0A\00"
@stdin = external global %struct._IO_FILE*
@double_fmt = private unnamed_addr constant [4 x i8] c"%f\0A\00"

define i32 @main() {
br label %IntOp
IntOp:
%a = alloca i32
store i32 0, i32* %a
%x = alloca i32
store i32 0, i32* %x
%y = alloca i32
store i32 0, i32* %y
%z = alloca i32
store i32 0, i32* %z
%multiply = alloca i32
store i32 0, i32* %multiply
%_t0 = alloca i32
store i32 100, i32* %_t0
store i32 100, i32* %x
%_t1 = alloca i32
store i32 50, i32* %_t1
store i32 50, i32* %y
%_t2 = alloca i32
store i32 10, i32* %_t2
store i32 10, i32* %z
call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t8fe02a3e_deb4_458d_af59_b492c74da090, i32 0, i32 0))

%_t4 = alloca i32
store i32 100, i32* %_t4
%_tc04c5b84_71d3_4e8f_9569_b1942dbe955e = load i32, i32* %x
call void @write_int(i32 %_tc04c5b84_71d3_4e8f_9569_b1942dbe955e)

call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._td5a85f85_0108_4eb3_98a0_e8a4e3626df9, i32 0, i32 0))

%_t6 = alloca i32
store i32 50, i32* %_t6
%_t9057ee5d_3158_48d0_9f1b_8a5e83415176 = load i32, i32* %y
call void @write_int(i32 %_t9057ee5d_3158_48d0_9f1b_8a5e83415176)

call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t47c512e0_5c6d_430c_b2c9_e7ca533c85d7, i32 0, i32 0))

%_t8 = alloca i32
store i32 10, i32* %_t8
%_tc94c7701_ebf3_4cc0_ba61_205f5dff9497 = load i32, i32* %z
call void @write_int(i32 %_tc94c7701_ebf3_4cc0_ba61_205f5dff9497)

%_t9 = alloca i32
store i32 5, i32* %_t9
store i32 5, i32* %a
call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._t96561801_b47b_4899_a5af_9a1c3ae95c5b, i32 0, i32 0))

%_t11 = alloca i32
store i32 5, i32* %_t11
%_t7e16c917_e395_40c3_83e6_2cb07beb814d = load i32, i32* %a
call void @write_int(i32 %_t7e16c917_e395_40c3_83e6_2cb07beb814d)

%_t12 = alloca i32
store i32 5, i32* %_t12
%_t13 = alloca i32
store i32 10, i32* %_t13
%a_val = load i32, i32* %a
%z_val = load i32, i32* %z
%_t14_val = mul i32 %a_val, %z_val
store i32 %_t14_val, i32* %multiply
call void @write_string(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str._t31f53cc9_91ca_4b13_af70_be6d74bb3d18, i32 0, i32 0))

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
